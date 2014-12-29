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

$(function() {
  // Override execute handler on code cells to copy metadata from ijava kernel into
  // cell metadata.
  var originalHandler = IPython.CodeCell.prototype._handle_execute_reply;

  IPython.CodeCell.prototype._handle_execute_reply = function(msg) {
    originalHandler.call(this, msg);

    var metadata = msg.metadata;
    for (var n in metadata) {
      if (n.indexOf('ijava.') === 0) {
        this.metadata[n] = metadata[n];
      }
    }
  }
});
