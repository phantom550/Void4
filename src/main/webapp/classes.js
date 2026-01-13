// Wait until DOM is loaded
document.addEventListener('DOMContentLoaded', function() {

    // --- Sidebar Toggle Logic (MD3 Drawer) ---
    var menuBtn = document.getElementById('menuBtn');
    var sidebar = document.querySelector('.sidebar');
    var scrim = document.querySelector('.scrim');

    function toggleMenu() {
        sidebar.classList.toggle('active');
        scrim.classList.toggle('active');
    }

    if(menuBtn) {
        menuBtn.addEventListener('click', toggleMenu);
    }

    if(scrim) {
        scrim.addEventListener('click', function() {
            sidebar.classList.remove('active');
            scrim.classList.remove('active');
        });
    }

    // --- Chart Logic ---
	// --- Chart Logic ---
	var ctxAttendance = document.getElementById('attendanceChart');
	if (ctxAttendance && attendanceData.length > 0) {
	    new Chart(ctxAttendance, {
	        type: 'line',
	        data: {
	            labels: attendanceData.map((_, i) => 'Student ' + (i + 1)),
	            datasets: [{
	                label: 'Attendance',
	                data: attendanceData,
	                borderColor: '#0061a4',
	                backgroundColor: 'rgba(0, 97, 164, 0.1)',
	                fill: true,
	                tension: 0.4,
	                pointRadius: 4,
	                pointBackgroundColor: '#0061a4'
	            }]
	        },
	        options: {
	            responsive: true,
	            maintainAspectRatio: false,
	            plugins: { legend: { display: false } },
	            scales: {
	                x: { grid: { display: false } },
	                y: { display: false }
	            }
	        }
	    });

	    // --- Calculate and display average ---
	    var total = 0;
	    for (var i = 0; i < attendanceData.length; i++) {
	        total += attendanceData[i];
	    }
	    var avg = Math.round(total / attendanceData.length);
	    var avgElement = document.getElementById('attendanceAvg');
	    if (avgElement) {
	        avgElement.innerText = avg + "%";
	    }
	}


	var ctxPerformance = document
	    .getElementById('performanceChart')
	    ?.getContext('2d');

	if (ctxPerformance && studentName.length && performance.length) {

	    if (window.performanceChartInstance) {
	        window.performanceChartInstance.destroy();
	    }

	    window.performanceChartInstance = new Chart(ctxPerformance, {
	        type: 'bar',
	        data: {
	            labels: studentName,
	            datasets: [{
	                label: 'Marks',
	                data: performance.map(Number),
	                backgroundColor: '#80f0e0',
	                borderRadius: 8,
	                barThickness: 22
	            }]
	        },
	        options: {
	            responsive: true,
	            maintainAspectRatio: false,
	            plugins: {
	                legend: { display: false }
	            },
	            scales: {
	                y: {
	                    beginAtZero: true,
	                    max: 100,
	                    ticks: { stepSize: 10 }
	                },
	                x: {
	                    grid: { display: false }
	                }
	            }
	        }
	    });
	}

    // --- Chip Interaction ---
    var chips = document.querySelectorAll('.chip');
    for(var i=0; i<chips.length; i++) {
        chips[i].addEventListener('click', function() {
            for(var j=0; j<chips.length; j++) {
                chips[j].classList.remove('active');
            }
            this.classList.add('active');
        });
    }

}); // end DOMContentLoaded


/* =========================================
   Modal & Other Logic (outside DOMContentLoaded)
   ========================================= */

// 1. Modal System
function openModal(modalId) {
    var modal = document.getElementById(modalId);
    if(modal) modal.classList.add('open');
}

function closeModal(modalId) {
    var modal = document.getElementById(modalId);
    if(modal) modal.classList.remove('open');
}

// 2. Notice Board
function showNotice(title, date, content) {
    var t = document.getElementById('noticeTitle');
    var d = document.getElementById('noticeDate');
    var c = document.getElementById('noticeContent');

    if(t) t.innerText = title;
    if(d) d.innerText = date;
    if(c) c.innerText = content;

    openModal('noticeModal');
}

// 3. Logout
function logoutAction() {
    if(confirm("Are you sure you want to logout?")) {
        alert("Logged out!");
        location.reload();
    }
}

// 4. AI API Integration
function callHumanizeApi() {
    var inputElement = document.getElementById('aiInput');
    var resultContainer = document.getElementById('aiResultContainer');
    var resultText = document.getElementById('aiResultText');
    var btn = document.getElementById('humanizeBtn');
    var btnText = document.getElementById('btnText');

    if(!inputElement) return;
    var textToProcess = inputElement.value;

    if(!textToProcess) {
        alert("Please enter some text first.");
        return;
    }

    btn.disabled = true;
    btnText.innerText = "Processing...";
    if(resultContainer) resultContainer.style.display = 'none';

    var url = 'https://chatgpt-42.p.rapidapi.com/aitohuman';
    var apiKey = 'YOUR_RAPIDAPI_KEY_HERE'; // Replace with your key

    var options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'x-rapidapi-key': apiKey,
            'x-rapidapi-host': 'chatgpt-42.p.rapidapi.com'
        },
        body: JSON.stringify({ text: textToProcess })
    };

    fetch(url, options)
    .then(function(response) {
        if(!response.ok) throw new Error("API Error: " + response.status);
        return response.json();
    })
    .then(function(result) {
        if(resultText) resultText.innerText = result.result || result.text || JSON.stringify(result);
        if(resultContainer) {
            resultContainer.style.display = 'block';
            if(resultContainer.classList) resultContainer.classList.add('fade-in');
        }
    })
    .catch(function(error) {
        console.error('Error:', error);
        if(resultText) {
            resultText.innerText = "Error: " + error.message;
            resultText.style.color = "#B3261E";
        }
        if(resultContainer) resultContainer.style.display = 'block';
    })
    .finally(function() {
        if(btn) btn.disabled = false;
        if(btnText) btnText.innerText = "Humanize";
    });
}
