// INCREASE - DECREASE AMOUNT COUNTER
function increaseOne() {
    var counter = document.getElementById("counter_number");
    var x = parseInt(counter.value, 10); // Convert to number
    counter.value = x + 1;
    amountOfCupcake = x + 1;
}

function decreaseOne() {
    var counter = document.getElementById("counter_number");
    var x = parseInt(counter.value, 10); //
    if(x>1) {// Convert to number
        counter.value = x - 1;
        amountOfCupcake = x - 1;
    }
}

// INFO BOX TOGGLES
document.querySelectorAll(".product_info_box").forEach(box =>{
   const header = box.querySelector(".product_description_header");
   header.addEventListener("click", () => {
    box.classList.toggle("open");
   });
});

//CART OVERLAY
function showCartOverlay(event) {
    event.preventDefault();

    const form = event.target;
    const amount = form.querySelector("input[name='amount']").value;
    const productName = form.querySelector("input[name='productName']").value;
    const thumb = form.querySelector("input[name='productThumbnail']").value;
    const price = form.querySelector("input[name='productPrice']").value;

    const overlay = document.getElementById("cart_overlay");
    const nameEl = document.getElementById("overlay_product_name");
    const amountEl = document.getElementById("overlay_product_amount");
    const overlayImage = document.getElementById("cart_overlay_image");
    const priceEl = document.getElementById("overlay_product_price");

    nameEl.textContent = productName;
    amountEl.textContent = "Antal: " + amount;
    priceEl.textContent = "Pris: " + (price * amount) + " kr.";
    overlayImage.src = thumb;

    document.getElementById("overlay_input_productName").value = productName;
    document.getElementById("overlay_input_amount").value = amount;
    document.getElementById("overlay_input_thumbnail").value = thumb;
    document.getElementById("overlay_input_price").value = price;

    overlay.classList.remove("hidden");
    setTimeout(() => overlay.classList.add("show"), 10);

    const closeButton = document.getElementById("cart_overlay_close");
    closeButton.onclick = () => {
      overlay.classList.remove("show");
      setTimeout(() => overlay.classList.add("hidden"), 350);
    };

    const removeButton = document.getElementById("overlay_remove_item");
    removeButton.onclick = () => {
        overlay.classList.remove("show");
        setTimeout(() => overlay.classList.add("hidden"));
    };
}

