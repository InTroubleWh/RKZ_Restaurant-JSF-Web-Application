document.addEventListener('DOMContentLoaded', function() {
    // Call all JS functions here

    // Scroll functionality for scroll sections
    function handleScrollAnimation() {
        // Get all scroll containers (in case there are multiple sections)
        var scrollContainers = document.querySelectorAll(".scroll-center-container");

        scrollContainers.forEach(function(scrollContainer) {
            var position = scrollContainer.getBoundingClientRect();

            // Check if the element is in the viewport
            if (position.top < window.innerHeight && position.bottom >= 0) {
                scrollContainer.classList.add("visible");
            }
        });
    }
// Scroll functionality for the newly added image section
window.addEventListener("scroll", function () {
    var imageContainer = document.querySelector("#scroll-image-container");
    var imagePosition = imageContainer.getBoundingClientRect();

    // Check if the image container is in the viewport
    if (imagePosition.top < window.innerHeight && imagePosition.bottom >= 0) {
        imageContainer.classList.add("visible"); // Apply visible class when in view
    }
});

    window.addEventListener("scroll", handleScrollAnimation);

    // Carousel functionality
    let currentIndex = 0; // Current slide index
    const images = document.querySelectorAll('.carousel-image'); // Get all carousel images

    function showNextImage() {
        images[currentIndex].classList.remove('active'); // Hide current image
        currentIndex = (currentIndex + 1) % images.length; // Move to the next index
        images[currentIndex].classList.add('active'); // Show the next image
    }

    // Automatically change the image every 3 seconds
    setInterval(showNextImage, 3000);
});