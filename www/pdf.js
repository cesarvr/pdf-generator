/*global cordova, module*/


function opts(options){

  options.documentSize = options.documentSize || "A4";
  options.landscape  = options.landscape || "portrait";
  options.orientation  = options.orientation || "portrait";
  options.type = options.type || "base64";
  options.fileName  = options.fileName || "default.pdf";
  options.baseUrl = options.baseUrl || null;

  return options;
}

function validate(param, message){
  if(param === '' ||
     param === undefined ||
     param === null || 
     typeof param !== 'string'
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
		 var baseUrl = options.baseUrl;

        cordova.exec(successCallback, errorCallback, "PDFService", "htmlToPDF", [ url, data, docSize, landscape, type, fileName, baseUrl ]);
    },

    fromURL: function(url, options){
      return new Promise(function(resolve, reject){
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
                                    options.fileName,
									options.baseUrl ]);
      })

    }
};
