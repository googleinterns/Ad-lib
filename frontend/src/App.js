import React from 'react';
import './App.css';
import MenuBar from './menu-bar.js';
import Form from './form.js'

// Add components and content to UI
function App() {
  return (
    <div>
      <MenuBar />
      <h1>Meet fellow Googlers <em>now</em>!</h1>
      <h4>Miss bumping into new faces at the office? Want an easy, fun, spontaneous way of meeting 
          Googlers virtually? Now you can!</h4>
      <h4>Ad-lib matches you with a fellow Googler in the queue, notifies you through email when 
          you’ve been matched, and adds an event to your Calendar with a Meet link for you to 
          join immediately! It also provides a starter question to get the conversation flowing!</h4>
      <Form />
    </div>
  );
}

export default App;
