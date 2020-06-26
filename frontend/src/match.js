// @Front-end: Call startMatch() on form submit
/** Add participant, find match if possible, keep searching for match every 5 seconds */
function startMatch() {
  addParticipant();
  searchMatch();
}

/** Add participant, find match with existing participants if possible */
function addParticipant() {
  const params = new URLSearchParams();
  fetch('/add-participant', {method: 'POST', body: params});
}

/** Search for match in list of all matches */
function searchMatch() {
  fetch('/search-match').then(response => response.json()).then((match) => {
    // No match yet
    if (match.id == 0) {
      // Search every 5 seconds
      setTimeout(searchMatch, 5000);
    }
    // Found match (eventually new page)
    // @Front-end: ADD 'match-container' to CSS!
    else {
      const matchElement = document.getElementById('match-container');
      matchLdapElement.innerText = match.firstldap + " " + match.secondLdap + " " + match.duration;
    }
  });
}