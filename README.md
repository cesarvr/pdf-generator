# Cordova Hello World Plugin

Simple plugin to generate (offline) pdf using device native capabilities. Also provide the mecanism to share the pdf to other apps like Mail, etc. 

## Using
Clone the plugin

    $ git clone https://github.com/cesarvr/pdf-generator

Create a new Cordova Project

    $ cordova create hello com.example.helloapp Hello
    
Install the plugin

    $ cd hello
    $ cordova plugin add ../pdf-generator
    

Example: 

This generates a pdf from a URL, it convert HTML to PDF and returns the file representation in base64.  

```js
 document.addEventListener('deviceready', function() { 
        
        pdf.htmlToPDF({
            url: this.$urlShare.val(),
            documentSize: "A4",
            landscape: "portrait",
            type: "base64"
        }, this.success, this.failure);
 
 });
```

The same but giving HTML without URL. 

```js
 document.addEventListener('deviceready', function() { 
        
     pdf.htmlToPDF({
            data: this.$raw.val(),
            documentSize: "A4",
            landscape: "portrait",
            type: "base64"
        }, this.success, this.failure);
 
 });
```





Install iOS or Android platform

    cordova platform add ios
    cordova platform add android
    
Run the code

    cordova run 

## More Info

For more information on setting up Cordova see [the documentation](http://cordova.apache.org/docs/en/4.0.0/guide_cli_index.md.html#The%20Command-Line%20Interface)

For more info on plugins see the [Plugin Development Guide](http://cordova.apache.org/docs/en/4.0.0/guide_hybrid_plugins_index.md.html#Plugin%20Development%20Guide)
