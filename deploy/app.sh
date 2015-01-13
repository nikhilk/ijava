#!/bin/sh

ipython notebook --config=/app/config.py --notebook-dir=/notebooks \
  --ip="*" --port=8080 \
  --matplotlib=inline --no-mathjax --no-script --quiet

