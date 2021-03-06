# IPython Configuration for ijava

import os

c = get_config()

# Kernel setup
kernel_path = os.path.join(os.path.dirname(__file__), 'ijava')
c.KernelManager.kernel_cmd = [
  kernel_path,
  '--logLevel:info',
  '--dep:joda-time.jar',
  '{connection_file}'
]

# Protocol signing settings
c.Session.key = b''
c.Session.keyfile = b''

# Static files
c.NotebookApp.extra_static_paths = [
  os.path.join(os.path.dirname(__file__))
]

