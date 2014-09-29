#!/bin/sh

ipython notebook --quiet \
  --KernelManager.kernel_cmd="['python', 'kernel.py', '{connection_file}']"

