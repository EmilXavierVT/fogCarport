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