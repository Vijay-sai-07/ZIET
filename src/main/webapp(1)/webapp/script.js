const favFoods = [];
const unFavFoods = [];

let meals=[];
let allergies=[];
let diseases=[];

function logout(){
	fetch('FinalBoss/logout')
}

document.querySelectorAll(".fuf").forEach(div => {
  div.addEventListener('input', () => {
	console.log("made input");
    if (div.innerHTML.trim() === '') {  // Check if the div is empty
      div.style.display = 'none';       // Hide the div
    }
    else{
		div.style.display = 'flex'; 
	}
  });
});

document.addEventListener('DOMContentLoaded', () => {
    // Theme Management
    const themeToggle = document.querySelector('.themeToggle');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)');
    
    // Initialize theme
    const savedTheme = localStorage.getItem('theme');
    const initialTheme = savedTheme || (prefersDark.matches ? 'dark' : 'light');
    document.documentElement.setAttribute('data-theme', initialTheme);
    themeToggle.checked = initialTheme === 'dark';

    // Theme toggle handler
    themeToggle.addEventListener('change', () => {
        const newTheme = themeToggle.checked ? 'dark' : 'light';
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
    });

    // Navigation functionality
    const navLinks = document.querySelectorAll('.nav-link');
    const pages = document.querySelectorAll('.page');

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            // Remove active class from all links and pages
            navLinks.forEach(l => l.classList.remove('active'));
            document.querySelector("#hero-content").classList.remove('activeFlex');

            pages.forEach(p => p.classList.remove('active'));

            // Add active class to clicked link
            link.classList.add('active');

            // Special handling for logo to return to hero
            if (link.classList.contains("logo")) {
                document.querySelector("#hero-content").classList.add('activeFlex');
                return;
            }

            // Show corresponding page for non-logo links
            const pageId = link.getAttribute('data-page');
            const targetPage = document.getElementById(pageId);
            if (targetPage) {
                targetPage.classList.add('active');
            }
        });
    });

    // Add hover effects to buttons
    const buttons = document.querySelectorAll('.start-now, .submit-btn');
    buttons.forEach(button => {
        button.addEventListener('mouseenter', () => {
            button.style.transform = 'scale(1.05)';
        });
        
        button.addEventListener('mouseleave', () => {
            button.style.transform = 'scale(1)';
        });
    });

    // Form submission handling
    const addFoodForm = document.querySelector('.add-food-form form');
    if (addFoodForm) {
        addFoodForm.addEventListener('submit', (e) => {
            e.preventDefault();
            // Here you would typically handle the form submission
            alert('Food added successfully!');
            addFoodForm.reset();
        });
    }
});

    document.getElementById("profileForm").addEventListener("submit", function(event) {
        let height = document.getElementById("height");
        let weight = document.getElementById("weight");

        if (height.value <= 80 && height.value <= 240) {
            alert("Height must be a positive number!");
            event.preventDefault();
        }

        if (weight.value <= 30 && height.value <= 140) {
            alert("Weight must be a positive number!");
            event.preventDefault();
        }
    });



    const tabs = document.querySelectorAll('.tab');
    const contents = document.querySelectorAll('.content');

    tabs.forEach(tab => {
      tab.addEventListener('click', () => {
        // Remove active classes
        tabs.forEach(t => t.classList.remove('act'));
        contents.forEach(c => c.classList.remove('act'));

        // Add act class to clicked tab and corresponding content
        tab.classList.add('act');
        document.getElementById(tab.dataset.target).classList.add('act');
      });
    });
    
function fetchFoodItems() {
    fetch("/FinalBoss/allFoods") // Replace with your actual servlet URL
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            if (Array.isArray(data.all_foods)) {
                populateFoodOptions("foodSuggestions", data.all_foods);

           } else {
                console.error("Invalid data format received", data);
            }
        })
        .catch(error => console.error("Error fetching food items:", error));
}

function populateFoodOptions(datalistId, items) {
    const datalist = document.getElementById(datalistId);
    datalist.innerHTML = ""; // Clear existing options

    items.forEach(item => {
        const option = document.createElement("option");
        option.value = item;
        datalist.appendChild(option);
    });
}

function UpdateChoices(inputId, containerClass, list) {
    const input = document.getElementById(inputId);
    const container = document.querySelector(containerClass);

    input.addEventListener("keydown", function (event) {
    if (event.key === "Enter") {  
        event.preventDefault();  
        const selectedValue = input.value.trim();
        if (selectedValue && isValidFood(selectedValue)) {
            if (!favFoods.includes(selectedValue) && !unFavFoods.includes(selectedValue)) {
                list.push(selectedValue);
                addToList(selectedValue, list, container);
                input.value = ""; 
            } else {
                alert(`"${selectedValue}" is already in one of the lists.`);
            }
        }
    }
});

const checkboxes = document.querySelectorAll(".checkbox"); // Select all elements with the "checkbox" class



    function isValidFood(value) {
        const options = [...document.getElementById("foodSuggestions").children].map(opt => opt.value);
        return options.includes(value);
    }

    function addToList(value, list, container) {
        const itemDiv = document.createElement("div");
        itemDiv.classList.add("food-item");
        itemDiv.dataset.value = value;

        const text = document.createElement("p");
        text.textContent = value;
        text.classList.add("hover-color");

        const removeBtn = document.createElement("button");
        removeBtn.type = 'button';
        removeBtn.textContent = "X";
        removeBtn.classList.add("remove-btn");
        removeBtn.onclick = function () {
            list.splice(list.indexOf(value), 1); // Remove from array
            itemDiv.remove();
        };

        itemDiv.appendChild(text);
        itemDiv.appendChild(removeBtn);
        container.appendChild(itemDiv);
    }
}



fetchFoodItems();
UpdateChoices("foodInputFav", ".favs", favFoods);
UpdateChoices("foodInputUnfav", ".unfavs", unFavFoods);



let username, EmailId, height, weight, physical_activity_level, isVeg, fitness_goals, Dietary_preference, Preferred_Meal_Timings, allergicPortfolio, metabolicPortfolio, FavouriteFoods, UnfavouriteFoods;

fetch("/FinalBoss/userDetails") // Replace with your actual API URL
    .then(response => {
        if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        return response.json();
    })
    .then(data => {

       if (data.user_data) {
    // Destructure the JSON object into variables
   ({ 
                username,
                EmailId,
                height,
                weight,
                physical_activity_level,
                isVeg,
                fitness_goals,
                Dietary_preference,
                Preferred_Meal_Timings,
                allergicPortfolio,
                metabolicPortfolio,
                FavouriteFoods,
                UnfavouriteFoods
            } = data.user_data);

    // Store the key-value pairs in an object
    const userInfo = {
        username,
        EmailId,
        height,
        weight,
        physical_activity_level,
        isVeg,
        fitness_goals,
        Dietary_preference,
        Preferred_Meal_Timings,
        allergicPortfolio,
        metabolicPortfolio,
        FavouriteFoods,
        UnfavouriteFoods
    };

            
            document.getElementById("height").value = height; // Default height value
			document.getElementById("weight").value = weight; // Default weight value
			let Mail=EmailId;
			document.getElementById("user").innerText = username;  // Check Dinner by default						        		
			document.getElementById(physical_activity_level).checked = true;

document.querySelectorAll('.radio-container input[name="radio2"]').forEach((radio) => {
  radio.addEventListener('change', printSelectedphysical_activity_level);
});



Preferred_Meal_Timings.split(",").forEach(meal => {
  const checkbox = document.getElementById(meal.trim());
  if (checkbox) checkbox.checked = true;
});


allergicPortfolio.split(",").forEach(allergy => {
    const checkboxId = allergy.trim(); // Map to checkbox ID

    if (checkboxId) {
        const checkbox = document.getElementById(checkboxId);
        if (checkbox) {
            checkbox.checked = true; // Check the corresponding box
        } 
    } else {
        console.warn(`No mapping found for allergy`);
    }
});

document.getElementById(fitness_goals).checked = true;

document.querySelectorAll('.radio-container input[name="radio1"]').forEach((radio) => {
  radio.addEventListener('change', printSelectedFitnessGoal);
});


metabolicPortfolio.split(",").forEach(disease => {
	const checkboxId = disease.trim(); // Map to checkbox ID

    if (checkboxId) {
        const checkbox = document.getElementById(checkboxId);
        if (checkbox) {
            checkbox.checked = true; // Check the corresponding box
        } 
    } else {
        console.warn(`No mapping found for allergy`);
    }
 
});


  const eggsToggle = document.getElementById('eggToggle');
  if(isVeg==0){
	eggsToggle.checked=true;
  }
  else{
	eggsToggle.checked=false;
  }
  
  
document.getElementById(Dietary_preference).checked = true;

FavouriteFoods.split(",").forEach(food => {

if(food.trim()!="None"){
	favFoods.push(food.trim());
addList(food.trim(), favFoods, document.querySelector(".favs"));
}

});

UnfavouriteFoods.split(",").forEach(food => {

if(food.trim()!="None"){
	unFavFoods.push(food.trim());
addList(food.trim(), unFavFoods, document.querySelector(".unfavs"));
}


});
        } else {
            console.error("Invalid data format received", data);
        }
    })
    .catch(error => console.error("Error fetching user data:", error));
    
function addList(value, list, container) {
        const itemDiv = document.createElement("div");
        itemDiv.classList.add("food-item");
        itemDiv.dataset.value = value;

        const text = document.createElement("p");
        text.textContent = value;
        text.classList.add("hover-color");

        const removeBtn = document.createElement("button");
        removeBtn.type = 'button';
        removeBtn.textContent = "X";
        removeBtn.classList.add("remove-btn");
        removeBtn.onclick = function () {
            list.splice(list.indexOf(value), 1); // Remove from array
            itemDiv.remove();
        };

        itemDiv.appendChild(text);
        itemDiv.appendChild(removeBtn);
        container.appendChild(itemDiv);
    }
    
    
    
function printSelectedphysical_activity_level() {
  const selectedRadio = document.querySelector('.radio-container input[name="radio2"]:checked');
  if (selectedRadio) {
    return selectedRadio.value;
  }
  return null; // Add a fallback return value
}


function printCheckedMeals() {
  const checkedMeals = Array.from(document.querySelectorAll('.meal-checkbox:checked')).map((checkbox) => checkbox.name);

  if (checkedMeals.length > 0) {
    checkedMeals.forEach((meal) => {
		meals.push(meal);
	});
  } else {
    console.log('❌ No meals selected.');
  }
}

function printCheckedAllergies() {
  const checkedMeals = Array.from(document.querySelectorAll('.allergy-checkbox:checked')).map((checkbox) => checkbox.value);

  if (checkedMeals.length > 0) {
    checkedMeals.forEach((allergy) => {
		allergies.push(allergy);
		});
  } else {
    console.log('❌ No meals selected.');
  }
}

function printSelectedFitnessGoal() {
  const selectedRadio = document.querySelector('.radio-container input[name="radio1"]:checked');
  if (selectedRadio) {
    return selectedRadio.value;
}}

function printCheckedDiseases() {
  const checkedDiseases = Array.from(document.querySelectorAll('.disease-checkbox:checked')).map((checkbox) => checkbox.id);

  if (checkedDiseases.length > 0) {
    checkedDiseases.forEach((disease) => {
		diseases.push(disease);
		});
  } else {
    console.log('❌ No diseases selected.');
  }
}

function printSelectedDietary_preference() {
  const selectedRadio = document.querySelector('.radio-container input[name="radio"]:checked');
  if (selectedRadio) {
   return selectedRadio.value;

}}
function updateProfile(){
	let eggss;
	if(isValidSubmit()){
		printSelectedphysical_activity_level();
		let eToggle=document.getElementById('eggToggle');
		if(eToggle.checked){
			eggss=0;
		}
		else{
			eggss=1;
		}
		const response = {
		  user_data: {
		    username: document.getElementById("user").innerText,
		    EmailId: EmailId,
		    height: document.getElementById("height").value,
		    weight: document.getElementById("weight").value,
		    physical_activity_level:printSelectedphysical_activity_level(),
		    isVeg: eggss,
		    fitness_goals: printSelectedFitnessGoal(),
		    Dietary_preference: printSelectedDietary_preference(),
		    Preferred_Meal_Timings: meals,
		    allergicPortfolio: allergies,
		    metabolicPortfolio: diseases,
		    FavouriteFoods: favFoods,
		    UnfavouriteFoods: unFavFoods
		  }
		}
    fetch('/FinalBoss/userDetailsUpdate', {  // 🔗 Replace with your servlet URL
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',  // ✅ Specify JSON content
        },
        body: JSON.stringify(response),  // 📦 Convert JS object to JSON string
    })
    .then(response => {
        if (!response.ok) {
        console.error('❌ Error updating profile:', error);
        }
        else{
			 console.log("✅ Profile updated successfully:");
		}
        return response;  // 📥 Handle JSON response if needed
    })
    .catch(error => {
        console.error('❌ Error updating profile:', error);
        alert("Failed to update profile.");
    });


// Replace 'YourServletURL' with the actual servlet URL
const servletURL = "/FinalBoss/userDetailsUpdate"; 

fetch(servletURL, {
  method: "POST",
  headers: {
    "Content-Type": "application/json"
  },
  body: JSON.stringify(response) // Convert JS object to JSON string
})
.then(res => {
  if (!res.ok) {
    throw new Error(`HTTP error! Status: ${res.status}`);
  }
  return res; // Assuming servlet returns JSON
})
.then(data => {
  console.log("Response from servlet:", data); // Handle successful response
})
.catch(error => {
  console.error("Error posting to servlet:", error); // Handle errors
});



}
else{
			alert("Choose atleast 2 meals!");
		}}
	
function isValidSubmit(){
	printCheckedMeals();
	 const checkedCount = document.querySelector(".checkbox-container").querySelectorAll(".checkbox:checked").length;
        
	if(meals.length<2){
		return false;
	}
        return true;
}
 let mealData = [];

  function formatDate(dateStr) {
    const date = new Date(dateStr); // Convert input string to Date object
    if (isNaN(date)) return "Invalid Date"; // Handle invalid date inputs

    const today = new Date();
    today.setHours(0, 0, 0, 0); // Normalize time to midnight

    const yesterday = new Date(today);
    yesterday.setDate(today.getDate() - 1); // Get yesterday's date

    const inputDate = new Date(date);
    inputDate.setHours(0, 0, 0, 0); // Normalize input date time

    if (inputDate.getTime() === today.getTime()) return "Today";
    if (inputDate.getTime() === yesterday.getTime()) return "Yesterday";

    return inputDate.toLocaleDateString('en-US', { 
        weekday: 'long', 
        month: 'long', 
        day: 'numeric', 
        year: 'numeric' 
    });
}

// Example Usage


 
 function getHistory() {
  const days = document.getElementById('date-range').value;
  const mealId = document.getElementById('meal-filter').value;
  const url = `/FinalBoss/historyOfMeals?days=${encodeURIComponent(days)}&mealId=${encodeURIComponent(mealId)}`;
  console.log(url);
  fetch(url, { method: 'GET' })
    .then(response => response.json())
    .then(data => {
		console.log("data",data);
		console.log(data["meal_response_history"].length);
		if(data==null){
			console.log("null data");
		}
		let container=document.querySelector(".noValues");
		if(data["meal_response_history"].length==0){
			container.style.display="flex";
			document.getElementById("meal-history").innerHTML = ""
			document.getElementById("meal-history").appendChild(container)
		}
		else if(data["meal_response_history"].length){
			/*container.style.display="none";*/
			let m=data["meal_response_history"];
			//m.sort((a,b) => new Date(b["DATE(response_date_time)"] - new Date(a["DATE(response_date_time)"])));
			console.log("meal_response_history", m)
			generateMealHistory(document.getElementById("meal-history"),m);
		}
      /*if(data["meal_response_history"].length===0){
		let container=document.querySelector(".noValues");
		console.log(container);
		      	container.style.display="flex";
	  }
	  else{
		let container=document.querySelector(".noValues");
		console.log(container);
		container.style.display="none";
	 	mealData=data["meal_response_history"];
      	generateMealHistory(document.getElementById("meal-history"),mealData);
	  }*/
     
    })
    .catch(error => console.error('Error:', error));
    
}
getHistory();

  function generateMealHistory(cont,mealData) { // add Id meal-history in main program
  		console.log("generateMealHistory called")
        const container = cont;
        let noValues = container.querySelector(".noValues")
        noValues.style.display = "none"
        console.log("From generateMealHistory", noValues)
        container.innerHTML = ""; // Clear previous content
        container.appendChild(noValues)
        const groupedMeals = {};
        mealData.forEach(meal => {
            if (!groupedMeals[meal.date]) groupedMeals[meal.date] = [];
            groupedMeals[meal.date].push(meal);
        });

        for (const date in groupedMeals) {
            const mealDay = document.createElement("div");
            mealDay.className = "history-day";

            const dayHeader = document.createElement("div");
            dayHeader.className = "day-header";
            dayHeader.innerHTML = `<h3>${formatDate(date)}</h3>`;

            let totalCalories = 0;
            groupedMeals[date].forEach(meal => totalCalories += meal.calories);
            dayHeader.innerHTML += `<span class="total-calories">${totalCalories} kcal</span>`;

            mealDay.appendChild(dayHeader);
          
            //   <span class="history-time">${meal.time}</span>
            groupedMeals[date].forEach(meal => {
				  const str = meal.foods;
					const result = str.replace(/"/g, ' ');  // Removes all double quotes

                const mealEntry = document.createElement("div");
                mealEntry.className = "history-item";
                mealEntry.innerHTML = `
                    <div class="meal-info">
                        <span class="history-meal">${meal.mealType}</span>
                   </div>
                    <div class="food-info">
                        <span class="history-food">${result}</span>
                        <div class="nutrition-info">
                            <span>${meal.calories} kcal</span>
                            <span>${meal.protein}g protein</span>
                            <span>${meal.carbs}g carbs</span>
                        </div>
                    </div>
                `;
                // mealEntry.addEventListener("click", () => {
                //     mealEntry.classList.toggle("selected");
                // });
                mealDay.appendChild(mealEntry);
            });

            container.appendChild(mealDay);
        }
    }
    function generateHistory(cont,mealData) { // add Id meal-history in main program
        const container = cont;
        container.innerHTML = ""; // Clear previous content
        const groupedMeals = {};
        mealData.forEach(meal => {
            if (!groupedMeals[meal.date]) groupedMeals[meal.date] = [];
            groupedMeals[meal.date].push(meal);
        });

        for (const date in groupedMeals) {
            const mealDay = document.createElement("div");
            mealDay.className = "history-day";
            const dayHeader = document.createElement("div");
            dayHeader.className = "day-header";

            let totalCalories = 0;
            groupedMeals[date].forEach(meal => totalCalories += meal.calories);
            dayHeader.innerHTML += `<span class="total-calories">${totalCalories} kcal</span>`;

            mealDay.appendChild(dayHeader);
          
            //   <span class="history-time">${meal.time}</span>
            groupedMeals[date].forEach(meal => {
				  const str = meal.foods;
					const result = str.replace(/"/g, ' ');  // Removes all double quotes

                const mealEntry = document.createElement("div");
                mealEntry.className = "history-item";
                mealEntry.innerHTML = `
                    <div class="meal-info">
                        <span class="history-meal">${meal.mealType}</span>
                   </div>
                    <div class="food-info">
                        <span class="history-food">${result}</span>
                        <div class="nutrition-info">
                            <span>${meal.calories} kcal</span>
                            <span>${meal.protein}g protein</span>
                            <span>${meal.carbs}g carbs</span>
                        </div>
                    </div>
                `;
                // mealEntry.addEventListener("click", () => {
                //     mealEntry.classList.toggle("selected");
                // });
                mealDay.appendChild(mealEntry);
            });

            container.appendChild(mealDay);
        }
    }
    
    function fetchCalories() {
	console.log("running");
    fetch('/FinalBoss/nutrientsPerDay').then(response => response.json())
    .then(data => {
        const mealLabels = [];
        const mealCalories = [];
        data.nutrients.forEach(element => {
            mealLabels.push(element.mealType);
            mealCalories.push(element.meal_calories);
        });
        if(mealLabels.length==0 || mealCalories.lenght==0){
			document.querySelector(".concentricChart").style.display="none";
		}
		else{
			        createDoughnutChart(mealLabels, mealCalories);

		}
        console.log("pppppppppppp");
        console.log(mealCalories);
        console.log(mealLabels);
    })
}
    
let myChart = null;

function createDoughnutChart(mealLabels, mealCalories) {
    console.log("DOUGHNUT");
    console.log("Initial Labels:", mealLabels);
    console.log("Initial Calories:", mealCalories);

    const canvas = document.getElementById('myConcentricChart');
    const ctx = canvas.getContext('2d');

    // ✅ Set width & height attributes to match CSS
    const container = canvas.parentElement;
    const computedStyles = window.getComputedStyle(container);

    const width = parseInt(computedStyles.width, 10);
    const height = parseInt(computedStyles.height, 10);

    canvas.setAttribute('width', width);   // Set attribute to match CSS width
    canvas.setAttribute('height', height); // Set attribute to match CSS height

    const totalCalories = mealCalories.reduce((sum, val) => sum + val, 0);

    // ✅ Destroy existing chart to avoid conflicts
    if (myChart) myChart.destroy();

    myChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: [...mealLabels],
            datasets: [{
                data: [...mealCalories],
                backgroundColor: ['#6A8EAE', '#3DFAFF', '#007EA7', '#003459', '#00374F'].slice(0, mealCalories.length),
                borderColor: Array(mealCalories.length).fill('#566e90'),
                borderWidth: 3,
                cutout: '60%'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false, // Let CSS and attributes control size
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: tooltipItem => {
                            const value = tooltipItem.raw;
                            const percentage = ((value / totalCalories) * 100).toFixed(2);
                            return `${percentage}%`;
                        }
                    }
                }
            }
        }
    });

    console.log(`Canvas Attributes -> width: ${canvas.getAttribute('width')}, height: ${canvas.getAttribute('height')}`);
    console.log("Meal Labels in Chart:", [...myChart.data.labels]);
    console.log("Meal Calories in Chart:", [...myChart.data.datasets[0].data]);
}


/*createDoughnutChart(mealLabels, mealCalories);
*/
  let barChart = null;

function createBarChart(inputLabels) {
    const canvas = document.getElementById('myBarChart');
    const ctx = canvas.getContext('2d');
    const container = canvas.parentElement;

    // ✅ Get computed styles from the parent container
    const computedStyles = window.getComputedStyle(container);
    const width = parseInt(computedStyles.width, 10);
    const height = parseInt(computedStyles.height, 10);

    // ✅ Set canvas attributes to match the container size
    canvas.setAttribute('width', width);
    canvas.setAttribute('height', height);

    // ✅ Destroy existing chart to prevent overlap
    if (barChart) barChart.destroy();


/*const rootStyles = getComputedStyle(document.documentElement);
const clr = rootStyles.getPropertyValue("--graph").trim();
console.log(clr);
console.log("asjfdtudsf");*/
    // ✅ Create a new responsive bar chart
 const rootStyles = getComputedStyle(document.documentElement);
let clr = rootStyles.getPropertyValue("--graph").trim();
clr = clr || "rgba(255, 0, 0, 1)"; // Fallback color

barChart = new Chart(ctx, {
    type: 'bar',
    data: {
        labels: ["Protein", "Fiber", "Carbohydrate", "Fat"],
        datasets: [{
            label: 'Nutrients consumed today',
            data: inputLabels,
            backgroundColor: [
                'rgba(255, 99, 132, 0.6)',
                'rgba(75, 192, 192, 0.6)',
                'rgba(54, 162, 235, 0.6)',
                'rgba(255, 206, 86, 0.6)'
            ],
            borderColor: [
                'rgba(255, 99, 132, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)'
            ],
            borderWidth: 1
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    font: {
                        size: 12,
                        color: getComputedStyle(document.documentElement).getPropertyValue("--graph").trim() // ✅ Color applied here
                    }
                }
            },
            x: {
                ticks: {
                    font: {
                        size: 12,
                        color: clr // ✅ Color applied here
                    }
                }
            }
        },
        plugins: {
            legend: {
                labels: {
                    boxWidth: 0,
                    usePointStyle: false,
                    font: {
                        size: 22,
                        color: clr // ✅ Color applied to legend text
                    }
                }
            }
        }
    }
});



    console.log(`Canvas size -> width: ${canvas.getAttribute('width')}, height: ${canvas.getAttribute('height')}`);
}

/*   createBarChart([100,80,90,50]);
 
const mealCalories = [100.67, 1000.78, 10.78];
 const mealLabels = ['Breakfast', 'Snack', 'Midnight Dinner'];
 createDoughnutChart(mealLabels,mealCalories);*/
 const ctx = document.getElementById('myBarChart').getContext('2d');
    function fetchBarChart() {
		function sum(list) {
			let j = 0;
			for (let i = 0; i < list.length; i++) {
				j+=list[i];
			}
			return j;
		}
        fetch('/FinalBoss/nutrientsPerDay')
        .then(response => response.json())
        .then(data => {
/*			console.log(data.nutrients);
*/			a = data.nutrients;
                const inputLabels = [
                    sum(data.nutrients.map(item => item.meal_proteins)),
                    sum(data.nutrients.map(item => item.meal_fibres)),
                    sum(data.nutrients.map(item => item.meal_carbohydrates)),
                    sum(data.nutrients.map(item => item.meal_fats))
                ];
/*                console.log(inputLabels);

*/               
if(inputLabels.length==0){
			document.querySelector(".workout-card").style.display="none";
		}
		else{
			createBarChart(inputLabels);
		} 
            })
        .catch(error => console.error('Error:', error));
    }
fetchCalories();
fetchBarChart();
	const canvas = document.getElementById('myConcentricChart');

    canvas.removeAttribute('width');
    canvas.removeAttribute('height');
    canvas.removeAttribute('style');




let Data;
function getDaysHistory() {
  const url = `/FinalBoss/historyOfMeals?days=1&mealId=0`;
  fetch(url, { method: 'GET' })
    .then(response => response.json())
    .then(data => {
	 	Data=data["meal_response_history"];
	 	console.log(Data);
	 		 	console.log(Data);
	 	console.log(Data);
	 	console.log(Data);
	 	console.log("ty");
	 		 	console.log("ty");
	 	console.log("ty");
	 	console.log("ty");
	 	console.log("ty");

      	generateHistory(document.getElementById("meal-list"),Data);
      		 	console.log("ty");
	 	console.log("ty");
	 	console.log("ty");
	 	console.log("ty");

     
    })
    .catch(error => console.error('Error:', error));
    
}
console.log("jagsc");
getDaysHistory();
/*
function generateMealsHistory() { // add Id meal-history in main program
        const container = document.getElementById("meal-list");
        container.innerHTML = ""; // Clear previous content
		console.log(Data);
        for (let i = 0; i < Data.length; i++) {
			const meal = Data[i];
			console.log(meal);
            const mealitem = document.createElement("div");
            mealitem.className = "meal-item";
            
            const meal_time = document.createElement("span");
            meal_time.className = "meal-time";

            const meal_name = document.createElement("span");
            meal_name.className = "meal-name";

            const meal_calories = document.createElement("span");
            meal_calories.className = "meal-calories";

            meal_time.innerText=meal.mealType;
            meal_name.innerText=meal.foods;
            meal_calories.innerText=meal.calories+" cals";
            

            mealitem.appendChild(meal_time);
            mealitem.appendChild(meal_name);
            mealitem.appendChild(meal_calories);

            container.appendChild(mealitem);
        }
    }*/
    function redirectToPage(){
		window.open("https://cliq.zoho.com/company/64396901/bots/testbotwq", '_blank');
	}
	
document.addEventListener('DOMContentLoaded',()=>{
	console.log("novalues div")
	console.log(document.querySelector(".noValues"));
})

