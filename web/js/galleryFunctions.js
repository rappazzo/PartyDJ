function setupGallery(name, galPointer, displayPointer, dollGalleryData) {
   $(name+'GalleryDisplay1').setOpacity(0);
   $(name+'GalleryImage'+dollDisplayPointer).src = dollGalleryData[0][dollGalPointer].src;
   $(name+'GalleryCaption'+dollDisplayPointer).innerHTML = dollGalleryData[1][dollGalPointer];
   $(name+'GallerySubtext'+dollDisplayPointer).innerHTML = dollGalleryData[2][dollGalPointer] != null ? dollGalleryData[2][dollGalPointer] : '';
}
function prev(name) {
	          new Effect.Opacity(name+'GalleryDisplay'+dollDisplayPointer, {from: 1.0, to: 0.0, duration: 1.0, queue: 'start'} );
		      if (--dollGalPointer < 0) dollGalPointer = dollGalleryImages.length - 1;
			  setImage();
		   }
		   function next() {
	          new Effect.Opacity(name+'GalleryDisplay'+dollDisplayPointer, {from: 1.0, to: 0.0, duration: 1.0, queue: 'start'} );
		      if (++dollGalPointer >= dollGalleryImages.length) dollGalPointer = 0;
			  setImage();
		   }
		   function setImage() {
		      dollDisplayPointer = dollDisplayPointer == 0 ? 1 : 0;
	          new Effect.Opacity(name+'GalleryDisplay'+dollDisplayPointer, {from: 0.0, to: 1.0, duration: 1.0, queue: 'end'} );
		      $(name+'GalleryImage'+dollDisplayPointer).src = dollGalleryImages[dollGalPointer].src;
		      $(name+'GalleryCaption'+dollDisplayPointer).innerHTML = dollGalleryCaptions[dollGalPointer];
              $(name+'GallerySubtext'+dollDisplayPointer).innerHTML = dollGalleryData[2][dollGalPointer] != null ? dollGalleryData[2][dollGalPointer] : '';
		   }