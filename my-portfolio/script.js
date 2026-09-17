const starterBooks = [
    { id: 1, title: "The Midnight Library", author: "Matt Haig", category: "fiction", categoryLabel: "Fiction", year: 2020, available: true, tone: "night" },
    { id: 2, title: "A Brief History of Time", author: "Stephen Hawking", category: "science", categoryLabel: "Science", year: 1988, available: true, tone: "space" },
    { id: 3, title: "The Design of Everyday Things", author: "Don Norman", category: "technology", categoryLabel: "Technology", year: 2013, available: false, tone: "design" },
    { id: 4, title: "Sapiens", author: "Yuval Noah Harari", category: "history", categoryLabel: "History", year: 2015, available: true, tone: "stone" },
    { id: 5, title: "The Creative Act", author: "Rick Rubin", category: "technology", categoryLabel: "Technology", year: 2023, available: true, tone: "red" },
    { id: 6, title: "The Book of Joy", author: "Dalai Lama & Desmond Tutu", category: "history", categoryLabel: "History", year: 2016, available: false, tone: "gold" }
];

const booksApi = {
    async getBooks() {
        const savedBooks = window.localStorage.getItem("library-books");
        return Promise.resolve(savedBooks ? JSON.parse(savedBooks) : starterBooks);
    },
    saveBooks(books) { window.localStorage.setItem("library-books", JSON.stringify(books)); }
};

const bookGrid = document.querySelector("#book-grid");
const searchInput = document.querySelector("#search-input");
const categoryFilter = document.querySelector("#category-filter");
const statusFilter = document.querySelector("#status-filter");
const resultCount = document.querySelector("#result-count");
const activeFilterLabel = document.querySelector("#active-filter-label");
const toast = document.querySelector("#toast");
const dialog = document.querySelector("#book-dialog");
const bookForm = document.querySelector("#book-form");
let books = [];
let newestFirst = true;
const savedBooks = new Set();

function showToast(message) {
    toast.textContent = message;
    toast.classList.add("visible");
    window.clearTimeout(showToast.timeout);
    showToast.timeout = window.setTimeout(() => toast.classList.remove("visible"), 2200);
}

function updateSummary() {
    document.querySelector("#total-count").textContent = books.length;
    document.querySelector("#available-count").textContent = books.filter(book => book.available).length;
    document.querySelector("#borrowed-count").textContent = books.filter(book => !book.available).length;
    document.querySelector("#available-books").innerHTML = books.filter(book => book.available).map(book => `<li>${book.title}</li>`).join("") || "<li>None</li>";
    document.querySelector("#borrowed-books").innerHTML = books.filter(book => !book.available).map(book => `<li>${book.title}</li>`).join("") || "<li>None</li>";
}

function coverMarkup(book) {
    const initials = book.title.split(" ").slice(0, 2).map(word => word[0]).join("");
    return `<div class="book-cover ${book.tone}" aria-hidden="true"><span>${initials}</span><small>${book.categoryLabel}</small></div>`;
}

function filteredBooks() {
    const query = searchInput.value.trim().toLowerCase();
    const category = categoryFilter.value;
    const status = statusFilter.value;
    return books.filter(book => {
        const matchesQuery = !query || `${book.title} ${book.author}`.toLowerCase().includes(query);
        const matchesCategory = category === "all" || book.category === category;
        const matchesStatus = status === "all" || (status === "available" ? book.available : !book.available);
        return matchesQuery && matchesCategory && matchesStatus;
    }).sort((first, second) => newestFirst ? second.year - first.year : first.title.localeCompare(second.title));
}

function renderBooks() {
    const visibleBooks = filteredBooks();
    resultCount.textContent = `Showing ${visibleBooks.length} of ${books.length} books`;
    activeFilterLabel.textContent = searchInput.value || categoryFilter.value !== "all" || statusFilter.value !== "all" ? "Filtered results" : "All books";
    updateSummary();
    bookGrid.innerHTML = visibleBooks.length ? visibleBooks.map(book => `<article class="book-card"><div class="book-card-top">${coverMarkup(book)}<button class="bookmark-button ${savedBooks.has(book.id) ? "saved" : ""}" type="button" data-save-id="${book.id}" aria-label="${savedBooks.has(book.id) ? "Remove" : "Save"} ${book.title}">${savedBooks.has(book.id) ? "♥" : "♡"}</button></div><div class="book-info"><span class="category-label">${book.categoryLabel}</span><h3>${book.title}</h3><p>${book.author}</p><div class="book-footer"><span class="availability ${book.available ? "available" : "borrowed"}"><i></i>${book.available ? "Available" : "Borrowed"}</span><span>${book.year}</span></div></div><button class="details-button" type="button" data-action="${book.available ? "borrow" : "return"}" data-book-id="${book.id}">${book.available ? "Borrow book" : "Return book"}<span>→</span></button></article>`).join("") : `<div class="empty-state"><strong>No books found</strong><p>Try a different search or clear the filters.</p></div>`;
}

function clearFilters() {
    searchInput.value = "";
    categoryFilter.value = "all";
    statusFilter.value = "all";
    renderBooks();
    showToast("Filters cleared");
}

searchInput.addEventListener("input", renderBooks);
categoryFilter.addEventListener("change", renderBooks);
statusFilter.addEventListener("change", renderBooks);
document.querySelector("#clear-button").addEventListener("click", clearFilters);
document.querySelector("#sort-button").addEventListener("click", () => {
    newestFirst = !newestFirst;
    document.querySelector("#sort-button").textContent = newestFirst ? "Sort: newest first" : "Sort: title A-Z";
    renderBooks();
});
document.querySelector("#add-book-button").addEventListener("click", () => {
    bookForm.reset();
    dialog.showModal();
});
document.querySelector("#close-dialog").addEventListener("click", () => dialog.close());
document.querySelector("#cancel-dialog").addEventListener("click", () => dialog.close());
bookForm.addEventListener("submit", event => {
    event.preventDefault();
    const formData = new FormData(bookForm);
    const category = formData.get("category");
    const categoryLabel = category.charAt(0).toUpperCase() + category.slice(1);
    books.push({ id: Date.now(), title: formData.get("title").trim(), author: formData.get("author").trim(), category, categoryLabel, year: new Date().getFullYear(), available: true, tone: "stone" });
    booksApi.saveBooks(books);
    dialog.close();
    renderBooks();
    showToast("Book added successfully");
});
document.querySelector("#export-button").addEventListener("click", () => {
    const rows = books.map(book => `${book.title},${book.author},${book.categoryLabel},${book.available ? "Available" : "Borrowed"}`);
    const file = new Blob([`Title,Author,Category,Status\n${rows.join("\n")}`], { type: "text/csv" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(file);
    link.download = "library-books.csv";
    link.click();
    URL.revokeObjectURL(link.href);
    showToast("Book list exported");
});
document.querySelector("#help-button").addEventListener("click", () => showToast("Use Borrow book and Return book to update the collection."));
bookGrid.addEventListener("click", event => {
    const saveButton = event.target.closest("[data-save-id]");
    const actionButton = event.target.closest("[data-book-id]");
    if (saveButton) {
        const id = Number(saveButton.dataset.saveId);
        savedBooks.has(id) ? savedBooks.delete(id) : savedBooks.add(id);
        renderBooks();
        showToast(savedBooks.has(id) ? "Book saved" : "Book removed from saved list");
        return;
    }
    if (actionButton) {
        const book = books.find(item => item.id === Number(actionButton.dataset.bookId));
        book.available = actionButton.dataset.action === "return";
        booksApi.saveBooks(books);
        renderBooks();
        showToast(book.available ? "Book returned" : "Book borrowed");
    }
});

booksApi.getBooks().then(data => { books = data; renderBooks(); });