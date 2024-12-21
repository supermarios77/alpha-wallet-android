override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // ... existing code ...

    val splashImage = findViewById<ImageView>(R.id.splash_image)
    val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
    splashImage.startAnimation(fadeIn)
    
    // Optional: Add animation listener if you want to do something after animation
    fadeIn.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            splashImage.alpha = 1f  // Make visible as animation starts
        }

        override fun onAnimationEnd(animation: Animation?) {
            // Animation finished
        }

        override fun onAnimationRepeat(animation: Animation?) {}
    })
} 