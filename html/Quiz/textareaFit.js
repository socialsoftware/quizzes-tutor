var textareaFit = (function() {

  return {

    resize: function (textareaID){
      $("#" + textareaID).each(function(index, elem){
          // This line will work with pure Javascript (taken from NicB's answer):
          elem.style.height = "";
          elem.style.height = elem.scrollHeight + 3 + 'px';
      });
    }

  }

})();
