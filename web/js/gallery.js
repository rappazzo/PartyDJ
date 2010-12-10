/**
 * @author Michael Rappazzo
 * @require prototype.js
 */

/*global window, document, Prototype, $, $A, $H, $break, Class, Element, Event, Control */

if(typeof(Prototype) == "undefined") {
    throw "Gallery requires Prototype to be loaded."; }
if(typeof(Effect.Opacity) == "undefined") {
    throw "Gallery requires Effect.Opacity to be loaded."; }

Gallery = Class.create({
    initialize: function(imageId, options){
        if(!$(imageId)) {
            throw "Gallery could not find the element: " + imageId; }
		this.containerName = imageId;
        this.galPointer = 0;
		this.images = new Array();
		this.captions = new Array();
        this.options = {
		   captionId: null,
		}
        Object.extend(this.options,options || {});
    },
    add: function(image_src, captionText) {
	   var i = this.images.length;
	   this.images[i]=new Image();
	   this.images[i].src=image_src;
	   this.captions[i] = captionText != null ? captionText : '';
	   if (i == 0) {
	      this.setImage();
	   }
    },
    prev: function(){
	    if (this.images.length > 0) {
		   if (--this.galPointer < 0) {
		      this.galPointer = this.images.length - 1;
		   }
		   this.setImage();
		}
    },
    next: function(){
	    if (this.images.length > 0) {
		   if (++this.galPointer >= this.images.length) {
		      this.galPointer = 0;
		   }
		   this.setImage();
		}
    },
    setImage: function(){
	  var image = $(this.containerName);
	  var newImageSrc = this.images[this.galPointer].src;
	  var caption = $(this.options.captionId);
	  var newCaption = this.captions[this.galPointer];
	  new Effect.Opacity(this.containerName, {from: 1.0, to: 0.0, duration: 1.0, queue: 'start', 
		 afterFinish: function() {
			image.src = newImageSrc;
			if (caption) {
			   caption.innerHTML = newCaption;
			}
		 }
	  } );
	  new Effect.Opacity(this.containerName, {from: 0.0, to: 1.0, duration: 1.0, queue: 'end'} );
    },
});
