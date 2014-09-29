# kernel.py
#
# This sample kernel is meant to be able to demonstrate using zmq for
# implementing a language backend (called a kernel) for IPython.
#
# To adjust debug output, set debug_level to:
#  0 - show no debugging information
#  1 - shows basic running information
#  2 - also shows loop details
#  3 - also shows message details
#
# ipython notebook --KernelManager.kernel_cmd="['python', 'kernel.py', 
#                                               '{connection_file}']"

from __future__ import print_function

import sys
import json
import hmac
import uuid
import errno
import hashlib
import datetime
import threading
from pprint import pformat

import zmq
from zmq.eventloop import ioloop, zmqstream
from zmq.error import ZMQError

# Globals:
DELIM = b"<IDS|MSG>"

decode = json.loads
encode = json.dumps
debug_level = 3
exiting = False
engine_id = str(uuid.uuid4())

# Utility functions:
def shutdown():
    global exiting
    exiting = True
    ioloop.IOLoop.instance().stop()

def dprint(level, *args, **kwargs):
    if level <= debug_level:
        print(":", *args, **kwargs)
        print("")
        sys.stdout.flush()

def msg_id():
    return str(uuid.uuid4())

def sign(msg_lst):
    h = auth.copy()
    for m in msg_lst:
        h.update(m)
    return h.hexdigest()

def new_header(msg_type):
    return {
            "date": datetime.datetime.now().isoformat(),
            "msg_id": msg_id(),
            "username": "kernel",
            "session": engine_id,
            "msg_type": msg_type,
        }

def deserialize_wire_msg(wire_msg):
    delim_idx = wire_msg.index(DELIM)
    identities = wire_msg[:delim_idx]
    m_signature = wire_msg[delim_idx + 1]
    msg_frames = wire_msg[delim_idx + 2:]
    
    m = {}
    m['header']        = decode(msg_frames[0])
    m['parent_header'] = decode(msg_frames[1])
    m['metadata']      = decode(msg_frames[2])
    m['content']       = decode(msg_frames[3])
    check_sig = sign(msg_frames)
    if check_sig != m_signature:
        raise ValueError("Signatures do not match")
    
    return identities, m

def send(stream, msg_type, content=None, parent_header=None, metadata=None, identities=None):
    header = new_header(msg_type)
    if content is None:
        content = {}
    if parent_header is None:
        parent_header = {}
    if metadata is None:
        metadata = {}
    
    msg_lst = [
        bytes(encode(header)), 
        bytes(encode(parent_header)), 
        bytes(encode(metadata)), 
        bytes(encode(content)),
    ]
    signature = sign(msg_lst)
    parts = [DELIM,
             signature, 
             msg_lst[0],
             msg_lst[1],
             msg_lst[2],
             msg_lst[3]]
    if identities:
        parts = identities + parts
    dprint(3, "Sent: ", parts)
    stream.send_multipart(parts)
    stream.flush()

def run_thread(loop, name):
    dprint(2, "Starting loop for '%s'..." % name)
    while not exiting:
        dprint(2, "%s Loop!" % name)
        try:
            loop.start()
        except ZMQError as e:
            dprint(2, "%s ZMQError!" % name)
            if e.errno == errno.EINTR:
                continue
            else:
                raise
        except Exception:
            dprint(2, "%s Exception!" % name)
            if exiting:
                break
            else:
                raise
        else:
            dprint(2, "%s Break!" % name)
            break

def heartbeat_loop():
    dprint(2, "Starting loop for 'Heartbeat'...")
    while not exiting:
        try:
            zmq.device(zmq.FORWARDER, heartbeat_socket, heartbeat_socket)
        except zmq.ZMQError as e:
            if e.errno == errno.EINTR:
                continue
            else:
                raise
        else:
            break


# Socket Handlers:
def shell_handler(msg):
    global execution_count
    dprint(1, "Shell: ", msg)
    position = 0
    identities, msg = deserialize_wire_msg(msg)

    # process request:

    if msg['header']["msg_type"] == "execute_request":
        dprint(1, "Executing: ", pformat(msg['content']["code"]))
        content = {
            'execution_state': "busy",
        }
        send(iopub_stream, 'status', content, parent_header=msg['header'])

        content = {
            'execution_count': execution_count,
            'code': msg['content']["code"],
        }
        send(iopub_stream, 'pyin', content, parent_header=msg['header'])

        content = {
            'execution_count': execution_count,
            'data': {"text/plain": "result!"},
            'metadata': {}
        }
        send(iopub_stream, 'pyout', content, parent_header=msg['header'])

        content = {
            'execution_state': "idle",
        }
        send(iopub_stream, 'status', content, parent_header=msg['header'])

        metadata = {
            "dependencies_met": True,
            "engine": engine_id,
            "status": "ok",
            "started": datetime.datetime.now().isoformat(),
        }
        content = {
            "status": "ok",
            "execution_count": execution_count,
            "user_variables": {},
            "payload": [],
            "user_expressions": {},
        }
        send(shell_stream, 'execute_reply', content, metadata=metadata,
            parent_header=msg['header'], identities=identities)
        execution_count += 1
    elif msg['header']["msg_type"] == "kernel_info_request":
        content = {
            "protocol_version": [4, 0],
            "ipython_version": [1, 1, 0, ""],
            "language_version": [0, 0, 1],
            "language": "spec",
        }
        send(shell_stream, 'kernel_info_reply', content, parent_header=msg['header'], identities=identities)
    else:
        dprint("unknown msg_type:", msg['header']["msg_type"])

def control_handler(wire_msg):
    global exiting
    dprint(1, "Control: ", wire_msg)
    identities, msg = deserialize_wire_msg(wire_msg)
    # Control message handler:
    if msg['header']["msg_type"] == "shutdown_request":
        shutdown()

def iopub_handler(msg):
    dprint(1, "IOPub: ", msg)

def stdin_handler(msg):
    dprint(1, "Stdin: ", msg)

def bind(socket, connection, port):
    if port <= 0:
        return socket.bind_to_random_port(connection)
    else:
        socket.bind("%s:%s" % (connection, port))
    return port


ioloop.install()

if len(sys.argv) > 1:
    dprint(1, "Loading kernel with args:", sys.argv)
    dprint(1, "Reading config file '%s'..." % sys.argv[1])
    config = decode("".join(open(sys.argv[1]).readlines()))
else:
    dprint(1, "Starting kernel with default args...")
    config = {
        'control_port'      : 0,
        'hb_port'           : 0,
        'iopub_port'        : 0,
        'ip'                : '127.0.0.1',
        'key'               : str(uuid.uuid4()),
        'shell_port'        : 0,
        'signature_scheme'  : 'hmac-sha256',
        'stdin_port'        : 0,
        'transport'         : 'tcp'
    }

connection = config["transport"] + "://" + config["ip"]
session_id = unicode(uuid.uuid4()).encode('ascii')
secure_key = unicode(config["key"]).encode("ascii")
signature_schemes = {"hmac-sha256": hashlib.sha256}
auth = hmac.HMAC(
    secure_key, 
    digestmod=signature_schemes[config["signature_scheme"]])
execution_count = 1

ctx = zmq.Context()

# Heartbeat:
heartbeat_socket = ctx.socket(zmq.REP)
config["hb_port"] = bind(heartbeat_socket, connection, config["hb_port"])

# IOPub/Sub:
iopub_socket = ctx.socket(zmq.PUB)
config["iopub_port"] = bind(iopub_socket, connection, config["iopub_port"])
iopub_stream = zmqstream.ZMQStream(iopub_socket)
iopub_stream.on_recv(iopub_handler)

# Control:
control_socket = ctx.socket(zmq.ROUTER)
config["control_port"] = bind(control_socket, connection, config["control_port"])
control_stream = zmqstream.ZMQStream(control_socket)
control_stream.on_recv(control_handler)

# Shell:
shell_socket = ctx.socket(zmq.ROUTER)
config["shell_port"] = bind(shell_socket, connection, config["shell_port"])
shell_stream = zmqstream.ZMQStream(shell_socket)
shell_stream.on_recv(shell_handler)

# Stdin:
stdin_socket = ctx.socket(zmq.ROUTER)
config["stdin_port"] = bind(stdin_socket, connection, config["stdin_port"])
stdin_stream = zmqstream.ZMQStream(stdin_socket)
stdin_stream.on_recv(stdin_handler)

dprint(1, "Config:", encode(config))
dprint(1, "Starting Loops...")

hb_thread = threading.Thread(target=heartbeat_loop)
hb_thread.daemon = True
hb_thread.start()

dprint(1, "Ready! Listening...")
ioloop.IOLoop.instance().start()

