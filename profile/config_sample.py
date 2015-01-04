# IPython Configuration

import os

c = get_config()

# Kernel setup
kernel_path = os.path.join(os.path.dirname(__file__), '..', 'build', 'ijava')
c.KernelManager.kernel_cmd = [
  kernel_path,
  '{connection_file}',
  '-e', 'SampleExtension',
  '-d', 'sample.jar'
]

# Protocol signing settings
c.Session.key = b''
c.Session.keyfile = b''

# Static files
c.NotebookApp.extra_static_paths = [
  os.path.join(os.path.dirname(__file__))
]

