// Custom.js
//

$(function() {
  // load CodeMirror mode for Java
  $.getScript('/static/components/codemirror/mode/clike/clike.js');

  // Configure CodeMirror settings
  var cmConfig = IPython.CodeCell.options_default.cm_config;
  cmConfig.mode = 'text/x-java';
  cmConfig.indentUnit = 2;
  cmConfig.smartIndent = true;
  cmConfig.autoClearEmptyLines = true;
});

