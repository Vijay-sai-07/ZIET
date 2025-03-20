const favFoods = [];
const unFavFoods = [];

let meals=[];
let allergies=[];
let diseases=[];
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
            console.log(link);

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
            console.log(response);
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            console.log("Received Data:", data);
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

checkboxes.forEach(checkbox => {
    checkbox.addEventListener("change", function () {
        console.log(checkbox.checked);
    });
});

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
            console.log(`Updated List:`, list);
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
        console.log(response);
        return response.json();
    })
    .then(data => {
        console.log("Received Data:", data);

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
            console.log("Height"+document.getElementById("height").value)
			document.getElementById("weight").value = weight; // Default weight value
			            console.log("weight"+document.getElementById("weight").value)
			            
			           let Mail=EmailId;

			document.getElementById("user").innerText = username;  // Check Dinner by default
						            console.log("user"+document.getElementById("user").innerText)
						        

/*			document.getElementById(physical_activity_level).checked=true;
*/		
document.getElementById(physical_activity_level).checked = true;

// Function to print the currently selected fitness goal


// Example: Print selected value when the selection changes
document.querySelectorAll('.radio-container input[name="radio2"]').forEach((radio) => {
  radio.addEventListener('change', printSelectedphysical_activity_level);
});

// 🔔 Call this function to print the initially selected goal on load

			// Meal string from which to determine which checkboxes to check
/*// Map meal names to their corresponding checkbox IDs
const mealToCheckboxId = {
  BreakFast: "checkbox-bf",
  Lunch: "checkbox-lunch",
  Snack: "checkbox-snacks",
  Dinner: "checkbox-dinner",
  Midnight_Dinner: "checkbox-midnight"
};*/

// Split the mealString and check the corresponding checkboxes
Preferred_Meal_Timings.split(",").forEach(meal => {
  const checkbox = document.getElementById(meal.trim());
  if (checkbox) checkbox.checked = true;
});





// Example allergicPortfolio string received from the backend

// Map allergy names to their corresponding checkbox IDs

// Split the string and check corresponding checkboxes
allergicPortfolio.split(",").forEach(allergy => {
    const checkboxId = allergy.trim(); // Map to checkbox ID

    if (checkboxId) {
        const checkbox = document.getElementById(checkboxId);
        if (checkbox) {
            checkbox.checked = true; // Check the corresponding box
        } else {
            console.warn(`Checkbox with ID "${checkboxId}" not found.`);
        }
    } else {
        console.warn(`No mapping found for allergy: ${trimmedAllergy}`);
    }
});




 // Set default radio button (e.g., select "Weight gain" by default)
document.getElementById(fitness_goals).checked = true;

// Function to print the currently selected fitness goal


// Example: Print selected value when the selection changes
document.querySelectorAll('.radio-container input[name="radio1"]').forEach((radio) => {
  radio.addEventListener('change', printSelectedFitnessGoal);
});

// 🔔 Call this function to print the initially selected goal on load



// ✅ Auto-check checkboxes based on diseases array
console.log(metabolicPortfolio);
metabolicPortfolio.split(",").forEach(disease => {
  const checkbox = document.getElementById(disease.trim());
  if (checkbox) checkbox.checked = true;
});

// ✅ Function to print all checked diseases


// 🔔 Call to print the initially checked diseases


  const eggsToggle = document.getElementById('eggToggle');
  if(isVeg==0){
	eggsToggle.checked=true;
  }
  else{
	eggsToggle.checked=false;
  }

  eggsToggle.addEventListener('change', () => {
    console.log(`Eggs Enabled: ${eggsToggle.checked}`);
  });
  
  
document.getElementById(Dietary_preference).checked = true;

// Function to print the currently selected fitness goal


// Example: Print selected value when the selection changes
document.querySelectorAll('.radio-container input[name="radio"]').forEach((radio) => {
  radio.addEventListener('change', printSelectedDietary_preference);
});

// 🔔 Call this function to print the initially selected goal on load



console.log(FavouriteFoods);
FavouriteFoods.split(",").forEach(food => {
  console.log(food.trim());
/*  addToList(food.trim(),".favs");
*/
if(food.trim()!="None"){
	favFoods.push(food.trim());
addList(food.trim(), favFoods, document.querySelector(".favs"));
}


 


});

console.log(UnfavouriteFoods);
UnfavouriteFoods.split(",").forEach(food => {
  console.log(food.trim());
/*  addToList(food.trim(),".favs");
*/

if(food.trim()!="None"){
	unFavFoods.push(food.trim());
addList(food.trim(), unFavFoods, document.querySelector(".unfavs"));
}


});

// ✅ Function to print all checked diseases
/*function printFavouriteFoods() {
  const checkedDiseases = Array.from(document.querySelectorAll('.disease-checkbox:checked')).map((checkbox) => checkbox.id);

  if (checkedDiseases.length > 0) {
    console.log('✅ Checked Diseases:');
    checkedDiseases.forEach((disease) => console.log(disease));
  } else {
    console.log('❌ No diseases selected.');
  }
}

// 🔔 Call to print the initially checked diseases
printFavouriteFoods();*/

            console.log("User Info:", userInfo);

         
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
            console.log(`Updated List:`, list);
            itemDiv.remove();
        };

        itemDiv.appendChild(text);
        itemDiv.appendChild(removeBtn);
        console.log(container);
        console.log(itemDiv);
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
    console.log('✅ Checked Meals:');
    checkedMeals.forEach((meal) => {
		console.log(meal);
		meals.push(meal);
		console.log(meals);
	});
  } else {
    console.log('❌ No meals selected.');
  }
}

function printCheckedAllergies() {
  const checkedMeals = Array.from(document.querySelectorAll('.allergy-checkbox:checked')).map((checkbox) => checkbox.value);

  if (checkedMeals.length > 0) {
    console.log('✅ Checked Allergies:');
    checkedMeals.forEach((allergy) => {
		console.log(allergy);
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
    console.log('✅ Checked Diseases:');
    checkedDiseases.forEach((disease) => {
		console.log(disease);
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

		console.log("mealslist"+meals);
function updateProfile(){
	let eggss;
	if(isValidSubmit()){
		console.log("mail"+EmailId);
		console.log("user"+document.getElementById("user").innerText);
		console.log("Height"+document.getElementById("height").value);
		console.log("weight"+document.getElementById("weight").value);
		printSelectedphysical_activity_level();
		let eToggle=document.getElementById('eggToggle');
		if(eToggle.checked){
			eggss=0;
		}
		else{
			eggss=1;
		}
		printSelectedFitnessGoal();
		printSelectedDietary_preference();
		console.log("mealslist"+meals);
		printCheckedAllergies();
		printCheckedDiseases();
		console.log(favFoods);
		console.log(unFavFoods);
		
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
		console.log(response);
		console.log(meals);
		

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
	
console.log(meals);
console.log("above is meals")
function isValidSubmit(){
	printCheckedMeals();
	 const checkedCount = document.querySelector(".checkbox-container").querySelectorAll(".checkbox:checked").length;
        console.log(document.querySelector(".checkbox-container").querySelectorAll(".checkbox:checked"));
        console.log("hjthhhhhhhhhhhh"+meals);
        
	if(meals.length<2){
		return false;
	}
       
        return true;
    
	
}


 let mealData = [];

    function formatDate(dateStr) {
        const date = new Date(dateStr);
        const today = new Date();
        const yesterday = new Date();
        yesterday.setDate(today.getDate() - 1);

        const todayStr = today.toISOString().split('T')[0];
        const yesterdayStr = yesterday.toISOString().split('T')[0];

        if (dateStr === todayStr) return "Today";
        if (dateStr === yesterdayStr) return "Yesterday";

        return date.toLocaleDateString('en-US', { 
            weekday: 'long', 
            month: 'long', 
            day: 'numeric', 
            year: 'numeric' 
        });
    }

 
 function getHistory() {
  const days = document.getElementById('date-range').value;
  const mealId = document.getElementById('meal-filter').value;
  // Construct URL with query parameters
  const url = `/FinalBoss/historyOfMeals?days=${encodeURIComponent(days)}&mealId=${encodeURIComponent(mealId)}`;
  fetch(url, { method: 'GET' })
    .then(response => response.json())
    .then(data => {
      console.log('Response from servlet:', data);
      mealData=data["meal_response_history"];
      generateMealHistory();
    })
    .catch(error => console.error('Error:', error));
    
}
getHistory();

  function generateMealHistory() { // add Id meal-history in main program
        const container = document.getElementById("meal-history");
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
            dayHeader.innerHTML = `<h3>${formatDate(date)}</h3>`;

            let totalCalories = 0;
            groupedMeals[date].forEach(meal => totalCalories += meal.calories);
            dayHeader.innerHTML += `<span class="total-calories">${totalCalories} kcal</span>`;

            mealDay.appendChild(dayHeader);
            //   <span class="history-time">${meal.time}</span>
            groupedMeals[date].forEach(meal => {
                const mealEntry = document.createElement("div");
                mealEntry.className = "history-item";
                mealEntry.innerHTML = `
                    <div class="meal-info">
                        <span class="history-meal">${meal.mealType}</span>
                   </div>
                    <div class="food-info">
                        <span class="history-food">${meal.foods}</span>
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



// start
   

  