#import "PDFGenerator.h"

@implementation PDFGenerator

@synthesize hasPendingOperation;


-( void (^)(NSError *error))GetErrorHandler:(CDVInvokedUrlCommand*)command   {
    
    void (^ErrorHandling)(NSError *error) = ^(NSError *error){
        
        
        CDVPluginResult *result = [CDVPluginResult
                                   resultWithStatus: CDVCommandStatus_OK
                                   messageAsString: [error description] ];
 
        [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
        self.hasPendingOperation = NO;
    };
    
    return ErrorHandling;
}

-(void (^)(NSData *data))GetPDFHandler:(CDVInvokedUrlCommand*)command setOptions:(NSString*) option  {
    
    return ^(NSData *data){
        
        if ([option isEqualToString:@"share"])
            [self Share:data setCommand:command];
       
        else if ([option isEqualToString:@"base64"])
            [self SendBase64:data setCommand:command];
        
        else
            [self NOOP:command];
        
    };
}

-(void) SendBase64:(NSData *)pdfData setCommand:(CDVInvokedUrlCommand*)command{
    
    CDVPluginResult *result = [CDVPluginResult
                               resultWithStatus:CDVCommandStatus_OK
                               messageAsString:[pdfData base64EncodedStringWithOptions:0]];
    
    
    NSLog(@"sending base64");
    [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
    self.hasPendingOperation = NO;
}

-(void) Share:(NSData *)pdfData setCommand:(CDVInvokedUrlCommand*)command{
    // Create a filePath for the pdf.
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString* fileName = [command argumentAtIndex:5 withDefault:@"file"];
    NSString *filePath = [documentsDirectory stringByAppendingPathComponent:fileName];
    
    // Save the PDF. UIDocumentInteractionController has to use a physical PDF, not just the data.
    [pdfData writeToFile:filePath atomically:YES];
    
    _docController = [UIDocumentInteractionController interactionControllerWithURL:[NSURL fileURLWithPath:filePath]];
    _docController.delegate = self;
    _docController.UTI = @"com.adobe.pdf";
    
    NSLog(@"opening menu");
    CGSize viewSize = self.viewController.view.bounds.size;
    CGRect areaRectForOpenMenu = CGRectMake(viewSize.width / 2, viewSize.height, 0.0, 0.0);
    BOOL result = [_docController presentOptionsMenuFromRect:areaRectForOpenMenu inView:self.viewController.view animated:YES];
    
    CDVPluginResult *ret = [CDVPluginResult
                               resultWithStatus:CDVCommandStatus_OK
                               messageAsBool:result];
    
    [self.commandDelegate sendPluginResult:ret callbackId:[command callbackId]];
}


-(void) NOOP:(CDVInvokedUrlCommand*)command{

    CDVPluginResult *result = [CDVPluginResult
                               resultWithStatus:CDVCommandStatus_OK
                               messageAsBool:NO];
    
    
    NSLog(@"noop");
    [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
    self.hasPendingOperation = NO;

}

- (void)htmlToPDF:(CDVInvokedUrlCommand*)command
{
    self.hasPendingOperation = YES;
    

    
    NSString* url  = [command argumentAtIndex:0 withDefault:NULL];
    NSString* data = [command argumentAtIndex:1 withDefault:NULL];
    NSString* type =  [command argumentAtIndex:2 withDefault:@"A4"];
    NSString* _landscape =  [command argumentAtIndex:3 withDefault:@"portrait"];
    NSString* option     = [command argumentAtIndex:4 withDefault:@"base64"];
	NSString* bUrl = [command argumentAtIndex:6 withDefault:NULL];

    
    BNPageSize pageSize;
    BOOL landscape = NO;
	NSURL *baseUrl = nil;
    
    if ([type isEqualToString:@"A3"]) {
        pageSize = BNPageSizeA3;
    }else{
        pageSize = BNPageSizeA4;
    }
    
    if ([_landscape isEqualToString:@"portrait"]) {
        landscape = NO;
    }else if([_landscape isEqualToString:@"landscape"]){
        landscape = YES;
    }
    
	if (bUrl != NULL) {
		if ([bUrl isEqualToString:@"BUNDLE"]) {
			baseUrl = [[NSBundle mainBundle] bundleURL];
		} else {
			baseUrl = [[NSURL alloc] initWithString:bUrl];
		}
	}
    
    if (url != NULL)
        self.htmlPdfKit = [BNHtmlPdfKit saveUrlAsPdf:[NSURL URLWithString:url]
                                            pageSize:pageSize
                                         isLandscape:landscape
                                             success:[self GetPDFHandler:command setOptions:option]
                                             failure:[self GetErrorHandler:command]];
    
    if (data != NULL){
        NSURL *base =  [[NSURL alloc] initWithString:[[self.webViewEngine.URL absoluteString] stringByDeletingLastPathComponent]];
        
        self.htmlPdfKit = [BNHtmlPdfKit saveHTMLAsPdf:data
                                             pageSize:pageSize
                                          isLandscape:landscape
										      baseUrl:base
                                              success:[self GetPDFHandler:command setOptions:option]
                                              failure:[self GetErrorHandler:command]];
    
    
       
          NSLog(@"url--> %@", [[base absoluteString] stringByDeletingLastPathComponent]);
    }
}


@end

