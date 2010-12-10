      var dollGalleryImages = new Array();
	  var dollGalleryCaptions = new Array();
	  
      var gallerySize = 15;
	  for (i=0; i<gallerySize; i++) {
	     var prefix = "";
		 if (i < 100) prefix += "0";
		 if (i < 10) prefix += "0";
		 dollGalleryImages[i]=new Image();
		 dollGalleryImages[i].src="images/doll/doll_"+(prefix+i)+".jpg";
      }
      var index = 0;
	  dollGalleryCaptions[index++] = 'Ali and Joe';
	  dollGalleryCaptions[index++] = 'Ali, hanging with some of her friends.';
	  dollGalleryCaptions[index++] = 'Joe, hanging with the boys!';
	  dollGalleryCaptions[index++] = 'The "Ali" Hairstyle<div class="subtext">(Available in black, brown, sandy, and blonde)</div>';
	  dollGalleryCaptions[index++] = 'The "Zoe" Hairstyle<div class="subtext">(Available in black, brown, sandy, and blonde)</div>';
	  dollGalleryCaptions[index++] = 'The "Cleo" Hairstyle<div class="subtext">(Available in black, brown, sandy, and blonde)</div>';
	  dollGalleryCaptions[index++] = 'The "Hannah" Hairstyle<div class="subtext">(Available in black, brown, sandy, and blonde)</div>';
	  dollGalleryCaptions[index++] = 'The "Daphne" Hairstyle<div class="subtext">(Available in black, brown, sandy, and blonde)</div>';
	  dollGalleryCaptions[index++] = 'Eye Colors<div class="subtext">(black, brown, blue, grey/green)</div>';
	  dollGalleryCaptions[index++] = 'So many clothing options to choose from, and they are all removable!';
	  dollGalleryCaptions[index++] = 'With a real button for a belly "button", a felt heart with your inital on it<br/> and an official birth certificate.....your cuddle companion is complete!';
	  dollGalleryCaptions[index++] = 'The Design Studio.';
	  dollGalleryCaptions[index++] = 'Making hand-made knickers!';
	  dollGalleryCaptions[index++] = 'Prepping t-shirt patterns.';
	  dollGalleryCaptions[index++] = 'Baskets of fabric for the skirt.';
	  
