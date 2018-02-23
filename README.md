# cordova-pdf-generator

Simple plugin to generate (offline) pdf. the plugin basically transform HTML to PDF and also provide the mechanism to share the pdf to other apps like Mail, etc. For now works in iOS and Android, if you want to add other platform feel free contribute.   

The iOS HTML to PDF transformation is based in this work [BNHtmlPdfKit](https://github.com/brentnycum/BNHtmlPdfKit), I just add a new method to allow transformation between plain HTML to PDF.

## Getting Started 

[![Demo](https://img.youtube.com/vi/PPHFUxzHH44/0.jpg)](https://www.youtube.com/watch?v=PPHFUxzHH44)


Here you can find a [starting guide](https://dzone.com/articles/how-to-write-a-html-to-pdf-app-for-androidios-usin).

## Features

- Generates a PDF document using a URL or HTML.
- Open-with menu, open the context menu and (push to cloud, print, save, mail, etc...).  
- Return the Base64 file representation back, so you can upload the file to a server (IOS & Android only).


## Supported Platforms

* Android
* iOS

## Installing the easy way (Cordova CLI)

    cordova plugin add cordova-pdf-generator


## Installing using Plugman

    cordova platform add ios
    plugman install --platform ios --project platforms/ios --plugin cordova-pdf-generator


## Installing using NPM

    npm install cordova-pdf-generator
    cordova plugins add node_modules/cordova-pdf-generator


## Installing the hard way.

Clone the plugin

    $ git clone https://github.com/cesarvr/pdf-generator

Create a new Cordova Project

    $ cordova create hello com.example.helloapp Hello

Install the plugin

    $ cd hello
    $ cordova plugin add ../pdf-generator



## API

Before using the plugin just make sure that the device is ready by listening to the onDeviceReady event:

```js
document.addEventListener('deviceready', function(){
  // start using cordova plugin here.
})

```


### Description

The plugin expose a global variable named **pdf**, this variable expose the following functions.

#### pdf.fromURL( url, options )

Creates a PDF using a URL, it download the document into an in memory Webkit object, and renders it into a PDF.

- **url** : Takes the URL with the HTML document you want to transform to PDF, once the document finish loading is render by webkit and transformed into a PDF file.


Example:

```js
let options = {
                documentSize: 'A4',
                type: 'base64'
              }

pdf.fromURL('http://www.google.es', options)
    .then(()=>'ok')
    .catch((err)=>console.err(err))
```

#### pdf.fromData( url, options )

Creates a PDF using string with the HTML representation, it download the document into an in memory Webkit object, and renders it into a PDF.

- **data** : Takes a string representing the HTML document, it load this in Webkit and creates a PDF.  

Example:

```js
let options = {
                documentSize: 'A4',
                type: 'base64'
              }

pdf.fromData('<html><h1>Hello World</h1></html>', options)
    .then((base64)=>'ok')   // it will
    .catch((err)=>console.err(err))
```

#### Options

##### documentSize

- Its take ```A4, A3, A2``` this specify the format of the paper, just available in iOS, in Android this option is ignored.

##### type

- ```base64``` it will return a Base64 representation of the PDF file. ```{ type: 'base64' } ``, is not type is provided this one is choosen by default. `

```js
let options = {
                documentSize: 'A4',
                type: 'base64'
              }

pdf.fromData('<html><h1>Hello World</h1></html>', options)
    .then((base64)=> console.log(base64) )   // returns base64:JVBERi0xLjQKJdPr6eEKMSAwIG9iago8PC9DcmVh...
    .catch((err)=>console.err(err))


```



- ```share``` It will delegate the file to the OS printing infraestructure, this basically will allow the user to handle the file himself using the mobile OS features available.

```js
let options = {
                documentSize: 'A4',
                type: 'share'
              }

pdf.fromData( '<html><h1>Hello World</h1></html>', options)
    .then((stats)=> console.log('status', stats) )   // ok..., ok if it was able to handle the file to the OS.  
    .catch((err)=>console.err(err))

```



##### filename

- You can specify the name of the PDF file.  

```js
let options = {
                documentSize: 'A4',
                type: 'share',
                fileName: 'myFile.pdf'
              }

pdf.fromData( '<html><h1>Hello World</h1></html>', options)
    .then((stats)=> console.log('status', stats) )   // ok..., ok if it was able to handle the file to the OS.  
    .catch((err)=>console.err(err))

```




#### Other Use Cases
##### Loading from Device Filesystem.

```js

      //Example: file:///android_asset/index.html

      function printInternalFile(param) {

        /* generate pdf using url. */
        if(cordova.platformId === 'ios') {

          // To use window.resolveLocalFileSystemURL, we need this plugin https://cordova.apache.org/docs/en/latest/reference/cordova-plugin-file/
          // You can add this by doing cordova plugin add cordova-plugin-file or
          // cordova plugin add https://github.com/apache/cordova-plugin-file
          window.resolveLocalFileSystemURL(cordova.file.applicationDirectory,
            (url) => {
              var file = param.replace('file:///android_asset/',url.nativeURL);

              pdf.fromURL(file, {
                  documentsize: 'a4',
                  landscape: 'portrait',
                  type: 'share'
              })
                .then((stats)=> this.preparetogobackground )
                .catch((err)=> this.showerror)
            },
            (err) =>
            console.log('error', err, '  args ->', arguments)
          );
        }else {
              pdf.fromURL(param, {
                  documentsize: 'a4',
                  landscape: 'portrait',
                  type: 'share'
              })
                .then((stats)=> this.preparetogobackground )
                .catch((err)=> this.showerror)
        }
    }
```

##### Ionic/Angular 2 Example:

```js
import { Component } from '@angular/core';

import { NavController } from 'ionic-angular';

declare var cordova:any;    //global;

@Component({
  selector: 'page-home',
  templateUrl: 'home.html'
})
export class HomePage {

  constructor(public navCtrl: NavController) {
      const before = Date.now();

            document.addEventListener('deviceready', () => {
                console.log('DEVICE READY FIRED AFTER', (Date.now() - before), 'ms');

                //generate the pdf.
                cordova.plugins.pdf.fromData( '<html> <h1>  Hello World  </h1> </html>', options )
                .then(()=>'ok')
                .catch((err)=>console.err(err))
  }

}

```

#### Deprecated

Here are examples to use the deprecated methods.

This generates a pdf from a URL, it convert HTML to PDF and returns the file representation in base64.  

```js
 document.addEventListener('deviceready', function() {

        pdf.htmlToPDF({
            url: 'http://www.google.es',
            documentSize: 'A4',
            landscape: 'portrait',
            type: 'base64'
        }, this.success, this.failure);

 });
```

The same but giving HTML without URL.

```js
 document.addEventListener('deviceready', function() {

     pdf.htmlToPDF({
            data: '<html> <h1>  Hello World  </h1> </html>',
            documentSize: 'A4',
            landscape: 'portrait',
            type: 'base64'
        }, this.success, this.failure);

 });

```

Opening the pdf with other app menu.

```js
 document.addEventListener('deviceready', function() {

     pdf.htmlToPDF({
            data: '<html> <h1>  Hello World  </h1> </html>',
            documentSize: 'A4',
            landscape: 'portrait',
            type: 'share' //use share to open the open-with-menu.
        }, this.success, this.failure);

 });
```



# Demo

- Cordova/Javascript [Demo](https://github.com/cesarvr/pdf-generator-example).
- Ionic/Angular 2 [Example](https://github.com/cesarvr/ionic2-basic-example)


Install iOS or Android platform

    cordova platform add ios
    cordova platform add android

Run the code

    cordova run ios
    cordova run android

## More Info

[here]:https://github.com/cesarvr/pdf-generator-example

For more information on setting up Cordova see [the documentation](http://cordova.apache.org/docs/en/4.0.0/guide_cli_index.md.html#The%20Command-Line%20Interface)

For more info on plugins see the [Plugin Development Guide](http://cordova.apache.org/docs/en/4.0.0/guide_hybrid_plugins_index.md.html#Plugin%20Development%20Guide)
