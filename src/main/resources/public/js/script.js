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

let cartItems = JSON.parse(localStorage.getItem('cartItems')) || [];

function addToCart(item) {
    // Check if item is already in cart
    const existingIndex = cartItems.findIndex(i =>
        i.name === item.name && i.price === item.price
    );
    if (existingIndex !== -1) {
        // Increase amount if already in cart
        cartItems[existingIndex].amount =
            parseInt(cartItems[existingIndex].amount) + parseInt(item.amount);
    } else {
        cartItems.push(item);
    }
    localStorage.setItem('cartItems', JSON.stringify(cartItems));
}

function renderCartOverlay() {
    const itemsContainer = document.getElementById("cart_items_container");
    itemsContainer.innerHTML = " "; // Clear

    cartItems.forEach(item => {
        const node = document.createElement('div');
        node.className = "cart_item";
        node.innerHTML = `
          <img src="${item.thumb}" alt="" style="width: 60px; vertical-align:middle;"/>
          <span>${item.name}</span>
          <span>Antal: ${item.amount}</span>
          <span>Pris: ${item.price * item.amount} kr.</span>
          <button onclick="removeItemFromCart(${item.name})">Remove</button>
      `;
        itemsContainer.appendChild(node);
    });
}


function showCartOverlay(event) {
    event.preventDefault();

    const form = event.target;
    const amount = form.querySelector("input[name='amount']").value;
    const productName = form.querySelector("input[name='productName']").value;
    const thumb = form.querySelector("input[name='productThumbnail']").value;
    const price = form.querySelector("input[name='productPrice']").value;

    addToCart({
        name: productName,
        amount: amount,
        price: price,
        thumb: thumb
    });

    renderCartOverlay();

    const overlay = document.getElementById("cart_overlay");
    overlay.classList.remove("hidden");
    setTimeout(() => overlay.classList.add("show"), 10);

    const closeButton = document.getElementById("cart_overlay_close");
    closeButton.onclick = () => {
        overlay.classList.remove("show");
        setTimeout(() => overlay.classList.add("hidden"), 350);
    };


    function removeItemFromCart(name) {
        cartItems = cartItems.filter(item => item.price !== name);
        localStorage.setItem('cartItems', JSON.stringify(cartItems));
        renderCartOverlay();
    }

}

