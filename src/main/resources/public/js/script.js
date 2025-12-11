// INCREASE - DECREASE AMOUNT COUNTER
function increaseOne() {
    var counter = document.getElementById("counter_number");
    var x = parseInt(counter.value, 10); // Convert to number
    counter.value = x + 1;
    amountOfCupcake = x + 1;
}

function decreaseOne() {
    var counter = document.getElementById("counter_number");
    var x = parseInt(counter.value, 10);
    if(x>1) { // Convert to number
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
let cart = JSON.parse(sessionStorage.getItem("cart")) || [];

function saveCart() {
    sessionStorage.setItem("cart", JSON.stringify(cart));
}

function addToCart(form) {
    const amount = Number(form.querySelector("input[name='amount']").value);
    const name = form.querySelector("input[name='productName']").value;
    const thumbnail = form.querySelector("input[name='productThumbnail']").value;
    const price = Number(form.querySelector("input[name='productPrice']").value);
    const carportID = Number(form.querySelector("input[name='carportID']").value);

    const item = {
        carportID,
        name,
        amount,
        thumbnail,
        price,
        totalPrice: price * amount
    };

    cart.push(item);
    saveCart();
    renderCartOverlay();
}

function renderCartOverlay() {
    const container = document.getElementById("cart_list_container");
    const overlay = document.getElementById("cart_overlay");

    if (!container || !overlay) return;

    overlay.classList.remove("hidden");
    overlay.classList.add("show");

    container.innerHTML = "";

    if (cart.length === 0) {
        container.innerHTML = "<p>Din kurv er tom.</p>";
        return;
    }

    cart.forEach((item, index) => {
        const div = document.createElement("div");
        div.className = "cart_item";

        div.innerHTML = `
            <img src="${item.thumbnail}" class="cart_item_image" alt="${item.name}" />
            <div class="cart_item_info">
                <p hidden>${item.carportID}</p>
                <strong style="font-size: 12px;">${item.name}</strong>
                <p style="font-size: 12px;">Antal: ${item.amount}</p>
                <p style="font-size: 12px;">Pris: ${item.totalPrice} kr.</p>
            </div>
            <button class="cart_delete_button" data-index="${index}">Fjern</button>`;

        container.appendChild(div);
    });

    document.querySelectorAll(".cart_delete_button").forEach(btn => {
        btn.onclick = () => {
            const index = btn.getAttribute("data-index");
            cart.splice(index, 1);
            saveCart();
            renderCartOverlay();
        };
    });
}

document.addEventListener("DOMContentLoaded", () => {
    if (cart.length > 0) renderCartOverlay();

    const overlayClose = document.getElementById("cart_overlay_close");
    if(overlayClose) {
        overlayClose.addEventListener("click", () => {
            const overlay = document.getElementById("cart_overlay");
            overlay.classList.remove("show");
            overlay.classList.add("hidden");
            document.body.classList.remove("cart_open");
        });
    }

    const overlayRemove = document.getElementById("overlay_remove_item");
    if(overlayRemove) {
        overlayRemove.addEventListener("click", () => {
            cart.pop();
            saveCart();
            renderCartOverlay();
        });
    }

    const cartBtn = document.getElementById("cart_btn");
    if(cartBtn) {
        cartBtn.addEventListener("click", e =>{
            e.preventDefault();
            renderCartOverlay();
        });
    }
});

//CART PAGE
function renderCartPage() {
    const container = document.getElementById("cart_page_container");
    container.innerHTML = "";

    if (cart.length === 0) {
        container.innerHTML = "<p class='display_container_vertical'>Din kurv er tom.</p>";
        return;
    }

    cart.forEach((item, index) => {
        const div = document.createElement("div");
        div.className = "cart_page_item";

        div.innerHTML = `
            <img src="${item.thumbnail}" class="cart_page_image" alt="${item.name}" />
            <div class="cart_item_info_page">
            <p hidden>${item.carportID}</p>
            <strong>${item.name}</strong>
            <p>Antal: ${item.amount}</p>
            <p>Pris: ${item.totalPrice} kr.</p>
            <button class="cart_delete_button" onclick="removeItem(${index})">Fjern</button>
            </div>
        `;

        container.appendChild(div);
    });
}

function removeItem(index) {
    cart.splice(index, 1);
    sessionStorage.setItem("cart", JSON.stringify(cart));
    renderCartPage();
}

document.addEventListener("DOMContentLoaded", renderCartPage);

function preparePayment() {
    if(cart.length > 0) {
    document.getElementById("carportID").value = cart[0].carportID;
    }
    clearCart();
}

function clearCart() {
    cart = [];
    sessionStorage.removeItem("cart");
}

// SECTION ADMIN TABS
const tabs = document.querySelectorAll('.tab');
const tabContents = document.querySelectorAll('.tab_content');

tabs.forEach(tab => {
   tab.addEventListener('click',() => {
       tabs.forEach(t => t.classList.remove('active'));
       tabContents.forEach(tc => tc.classList.remove('active'));

       tab.classList.add('active');
       const target = tab.getAttribute('data-target');
       document.getElementById(target).classList.add('active');
   });
});

document.querySelector('.tab.active').click();

// Searchfields for admin/alert

const searchInputMaterials = document.getElementById('search');
const itemsList = [
    document.getElementById('materials_section'),
    document.getElementById('products_section'),
    document.getElementById('notifications_section'),
    document.getElementById('users_section')
];

searchInputMaterials.addEventListener('input', function()
{

    const query = searchInputMaterials.value.toLowerCase();
    itemsList.forEach(section=> {
        const table = section.querySelector("table");

        if (table) {

            const rows = table.getElementsByTagName('tr');

            for (let i = 1; i < rows.length; i++) {
                const tds = rows[i].getElementsByTagName('td');
                let rowText = '';

                for (let j = 0; j < tds.length; j++) {
                    rowText += tds[j].textContent.toLowerCase() + ' ';
                }
                rows[i].style.display = rowText.includes(query) ? '' : 'none';
            }
        }
    });
});

//ADMIN OVERLAY VIEW AND EDIT
document.querySelectorAll(".open_details").forEach(svg => {
    svg.addEventListener("click", function() {
        document.getElementById("length_input").value = this.dataset.length;
        document.getElementById("width_input").value = this.dataset.width;
        document.getElementById("shed_input").value = this.dataset.shed;
        document.getElementById("roof_input").value = this.dataset.roof;
        document.getElementById("shed_depth_input").value = this.dataset.shedDepth;
        document.getElementById("shed_width_input").value = this.dataset.shedWidth;

        document.getElementById("view_length").textContent = this.dataset.length;
        document.getElementById("view_width").textContent = this.dataset.width;
        document.getElementById("view_roof").textContent = this.dataset.roof;
        document.getElementById("view_shed").textContent = this.dataset.shed;
        document.getElementById("view_shed_depth").textContent = this.dataset.shedDepth;
        document.getElementById("view_shed_width").textContent = this.dataset.shedWidth;

        document.getElementById("view_shed").textContent =
            this.dataset.shed === true ? 'Ja' : 'Nej';

        document.getElementById("view_roof").textContent =
            this.dataset.roof === "1" ? "Ingen tag" :
            this.dataset.roof === "2" ? "Plast tag" : "";

        document.body.style.overflow = 'hidden';
        document.getElementById("edit_modal").classList.remove("modal_hidden");
        document.getElementById("view_panel").classList.remove("view_panel_hidden")
    });
});

document.getElementById("close_btn").addEventListener("click", () => {
    document.getElementById("edit_modal").classList.add("modal_hidden");
    document.getElementById("view_panel").classList.add("view_panel_hidden");
    document.body.style.overflow = 'unset';
});
