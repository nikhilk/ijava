#!/bin/sh

ipython notebook --config=../profile/config_sample.py \
  --notebook-dir=../notebooks \
  --ip="*" --port=9999 \
  --matplotlib=inline --no-mathjax --no-script --quiet

