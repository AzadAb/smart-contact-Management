const toggleSidebar = () => {
	const sidebar = document.querySelector('.sidebar');
	const content = document.querySelector('.content');

	if (window.innerWidth <= 600) {

		sidebar.classList.toggle('active');
	} else {

		sidebar.classList.toggle('hidden');


		if (sidebar.classList.contains('hidden')) {
			content.style.marginLeft = '0';
		} else {
			content.style.marginLeft = '20%';
		}
	}
};


const search = () => {
	let query = document.querySelector("#search-input").value.trim();

	if (query === '') {
		document.querySelector(".search-result").style.display = "none";
	} else {
		console.log(query);

		let url = `http://localhost:8080/search/${query}`;
		fetch(url)
			.then((response) => response.json())
			.then((data) => {
				console.log(data);

				let text = `<div class='list-group'>`;

				data.forEach((contact) => {
					text += `<a href='/user/contacts/${contact.cId}' class='list-group-item list-group-item-action'>${contact.name}</a>`;
				});

				text += `</div>`;

				const resultBox = document.querySelector(".search-result");


				if (resultBox) {
					resultBox.innerHTML = text;
					resultBox.style.display = "block";
				} else {
					console.error("Element .search-result not found.");
				}
			})
			.catch((error) => {
				console.error("Fetch error:", error);
			});
	}
};






 


