#import <Cordova/CDV.h>
#import "BNHtmlPdfKit.h"

@interface PDFGenerator : CDVPlugin <UIDocumentInteractionControllerDelegate>


@property UIDocumentInteractionController *docController;
@property  BNHtmlPdfKit *htmlPdfKit;

@property (readwrite, assign) BOOL hasPendingOperation;


- (void) htmlToPDF:(CDVInvokedUrlCommand*)command;

@end