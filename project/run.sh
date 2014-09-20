#!/bin/sh

mkdir -p /tmp/notebooks

ipython notebook --ip="0.0.0.0" --port=8080 --notebook-dir=/tmp/notebooks \
  --config=../profile/config.py \
  --matplotlib=inline --no-mathjax

