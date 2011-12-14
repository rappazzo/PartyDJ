/**
 Creates a swipe gesture event handler
*/
function SwipeDetector(id) {
    // Constants
    this.HORIZONTAL     = 1;
    this.VERTICAL       = 2;
    this.AXIS_THRESHOLD = 30;  // The user will not define a perfect line
    this.GESTURE_DELTA  = 60;  // The min delta in the axis to fire the gesture

    // Public members
    this.direction    = this.HORIZONTAL;
    this.element      = document.getElementById(id);
    this.onswiperight = null;
    this.onswipeleft  = null;
    this.onswipeup    = null;
    this.onswipedown  = null;
    this.inGesture    = false;

    // Private members
    this._originalX = 0
    this._originalY = 0
    var _this = this;
    // Makes the element clickable on iPhone
    this.element.onclick = function() {void(0)};

    var mousedown = function(event) {
         // Finger press
         event.preventDefault();
         _this.inGesture  = true;
         _this._originalX = (event.touches) ? event.touches[0].pageX : event.pageX;
         _this._originalY = (event.touches) ? event.touches[0].pageY : event.pageY;
         // Only for iPhone
         if (event.touches && event.touches.length!=1) {
             _this.inGesture = false; // Cancel gesture on multiple touch
         }
    };

    var mousemove = function(event) {
         // Finger moving
         event.preventDefault();
         var delta = 0;
         // Get coordinates using iPhone or standard technique
         var currentX = (event.touches) ? event.touches[0].pageX : event.pageX;
         var currentY = (event.touches) ? event.touches[0].pageY : event.pageY;

         // Check if the user is still in line with the axis
         if (_this.inGesture) {
             if ((_this.direction==_this.HORIZONTAL)) {
                 delta = Math.abs(currentY-_this._originalY);
             } else {
                 delta = Math.abs(currentX-_this._originalX);
             }
             if (delta >_this.AXIS_THRESHOLD) {
                 // Cancel the gesture, the user is moving in the other axis
                 _this.inGesture = false;
             }
         }

         // Check if we can consider it a swipe
         if (_this.inGesture) {
             if (_this.direction==_this.HORIZONTAL) {
                 delta = Math.abs(currentX-_this._originalX);
                 if (currentX>_this._originalX) {
                    direction = 0;
                 } else {
                    direction = 1;
                 }
             } else {
                 delta = Math.abs(currentY-_this._originalY);
                 if (currentY>_this._originalY) {
                    direction = 2;
                 } else {
                    direction = 3;
                 }
             }

             if (delta >= _this.GESTURE_DELTA) {
                 // Gesture detected!
                 var handler = null;
                 switch(direction) {
                      case 0: handler = _this.onswiperight; break;
                      case 1: handler = _this.onswipeleft; break;
                      case 2: handler = _this.onswipedown; break;
                      case 3: handler = _this.onswipeup; break;
                 }
                 if (handler!=null) {
                     // Call to the callback with the optional delta
                     handler(delta);
                 }
                 _this.inGesture = false;
             }

         }
    };


    // iPhone and Android's events
    this.element.addEventListener('touchstart', mousedown, false);
    this.element.addEventListener('touchmove', mousemove, false);
    this.element.addEventListener('touchcancel', function() {
        _this.inGesture = false;
    }, false);

    // We should also assign our mousedown and mousemove functions to
    // standard events on compatible devices
}

