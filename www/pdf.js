/*global cordova, module*/


function opts(options){

  options.documentSize = options.documentSize || "A4";
  options.landscape  = options.landscape || "portrait";
  options.type = options.type || "base64";
  options.fileName  = options.fileName || "default.pdf";

  return options;
}

function validate(param, message){
  if(param === '' ||
     param === undefined ||
     param === null
   )
    throw message
}


module.exports = {
    htmlToPDF: function (options, successCallback, errorCallback) {

         if(!options.url && !options.data) throw "No URL or HTML Data found.";

         var url = options.url;
         var data = options.data;
         var docSize = options.documentSize || "A4";
         var landscape = options.landscape || "portrait";
         var type = options.type || "base64";
         var fileName = options.fileName || "default.pdf";

        cordova.exec(successCallback, errorCallback, "PDFService", "htmlToPDF", [ url, data, docSize, landscape, type, fileName ]);
    },

    fromURL: function(url, options){
      return new Promise(function(resolve, reject){
debugger
        validate(url, "URL is required")
        options = opts(options)

        cordova.exec(resolve,
                     reject,
                     "PDFService",
                     "htmlToPDF", [ url,
                                    null,
                                    options.documentSize,
                                    options.landscape,
                                    options.type,
                                    options.fileName ]);

      })

    },

    fromData: function(data, options){
      return new Promise(function(resolve, reject){
        debugger
        validate(data, "String with HTML format is required")
        options = opts(options)

        cordova.exec(resolve,
                     reject,
                     "PDFService",
                     "htmlToPDF", [ null,
                                    data,
                                    options.documentSize,
                                    options.landscape,
                                    options.type,
                                    options.fileName ]);
      })

    }
};
