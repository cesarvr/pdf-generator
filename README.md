# Cordova Hello World Plugin

Simple plugin to generate (offline) pdf using device native capabilities. Also provide the mecanism to share the pdf to other apps like Mail, etc. 

For now the only ecosystem supported is IOS, but soon will make this plugin available for Android/Windows.

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
            url: "http://www.google.es",
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
            data: "<html> <h1>  Hello World  </h1> </html>",
            documentSize: "A4",
            landscape: "portrait",
            type: "base64"
        }, this.success, this.failure);
 
 });
```

# API reference

#### pdf

- data: here you sent raw HTML.
- url: url location of the webpage. 
- documentSize: for now only support "A4" and "A3". 
- landscape: portrait or landscape. 
- type:
    - *base64* give you the pdf in Base64 format so you can choose the next step. 
    - *share* opens IOS menu with all options available, this came handy when you want IOS take ownership of the Doc.  



Install iOS or Android platform

    cordova platform add ios
    cordova platform add android
    
Run the code

    cordova run 

## More Info

For more information on setting up Cordova see [the documentation](http://cordova.apache.org/docs/en/4.0.0/guide_cli_index.md.html#The%20Command-Line%20Interface)

For more info on plugins see the [Plugin Development Guide](http://cordova.apache.org/docs/en/4.0.0/guide_hybrid_plugins_index.md.html#Plugin%20Development%20Guide)
