# IPython Configuration

import os

c = get_config()

kernel_path = os.path.join(os.path.dirname(__file__), '..', 'build', 'ijava')
c.KernelManager.kernel_cmd = [ kernel_path, '{connection_file}' ]

c.Session.key = b''
c.Session.keyfile = b''

