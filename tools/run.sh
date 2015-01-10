#!/bin/sh

ipython notebook --config=../ipython/config.py --notebook-dir=../notebooks \
  --ip="*" --port=9999 \
  --matplotlib=inline --no-mathjax --no-script --quiet

