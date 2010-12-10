      var partyGalleryImages=new Array();
	  var partyGalleryCaptions = new Array();
	  
      var partyGallerySize = 7;
	  for (i=0; i<partyGallerySize; i++) {
	     var prefix = "";
		 if (i < 100) prefix += "0";
		 if (i < 10) prefix += "0";
		 partyGalleryImages[i]=new Image();
		 partyGalleryImages[i].src="images/party/party_"+(prefix+i)+".jpg";
	     partyGalleryCaptions[i] = '';
      }
	  partyGalleryCaptions[0] = '';