#!/bin/sh

IPY_CONFIG=../ipython/ijava-config.py
if [ "$1" = "-ext" ]; then
  IPY_CONFIG=../ipython/ijava-extended-config.py
fi

ipython notebook --config=$IPY_CONFIG --notebook-dir=../notebooks \
  --ip="*" --port=9999 \
  --matplotlib=inline --no-mathjax --no-script --quiet

