import React from 'react';
import axios from 'axios';
import 'date-fns';
import DateFnsUtils from '@date-io/date-fns';
import {MuiPickersUtilsProvider,
  KeyboardTimePicker} from '@material-ui/pickers';
import {makeStyles} from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import MatchPreference from './MatchPreference';
import Checkbox from './PreferencesCheckbox';
import RoleDropdown from './RoleDropdown';
import ProductAreaDropdown from './ProductAreaDropdown';
import DurationDropdown from './DurationDropdown';

/** Establishes style to use on rendering form component */
const useStyles = makeStyles((theme) => ({
  flexStartDiv: {
    display: 'flex',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    margin: theme.spacing(2),
  },
  flexEndDiv: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginTop: theme.spacing(2),
    marginBottom: theme.spacing(1),
    marginRight: theme.spacing(1),
  },
  section: {
    margin: theme.spacing(2),
    width: 650,
  },
  heading: {
    marginLeft: theme.spacing(2),
  },
  padding: {
    marginTop: theme.spacing(4),
    marginLeft: theme.spacing(2),
  },
}));

/**
 * Validates from inputs on submission and alerts user on error
 * @return {Boolean}  true or false based on validity of inputs
 * @param {String} role
 * @param {String} productArea
 * @param {Number} duration
 * @param {Date} timeAvailableUntil
 * @param {Number} currentTimeMilliseconds
 */
export function validateFormInputs(role, productArea, duration,
    timeAvailableUntil, currentTimeMilliseconds) {
  if (!(timeAvailableUntil instanceof Date) ||
    isNaN(timeAvailableUntil.getTime())) {
    alert('Please select a valid date.');
    return false;
  }
  const timeAvailableUntilMilliseconds = timeAvailableUntil.getTime();
  const durationMilliseconds = duration * 60000;
  if (currentTimeMilliseconds + durationMilliseconds >=
    timeAvailableUntilMilliseconds) {
    // Check if a meeting is possible with the provided inputs
    alert('Please select an larger time availability window');
    return false;
  } else if (productArea === '' || role === '') {
    alert('Please select options for all form fields to submit!');
    return false;
  } else {
    return true;
  }
}

/**
 * Create form component with time and match preference inputs
 * @return {Form} Form component
 */
export default function Form() {
  const classes = useStyles();

  // Declare state variables for each input field and set default states
  const [timeAvailableUntil, setTimeAvailableUntil] =
    React.useState(new Date());
  const [duration, setDuration] = React.useState(15);
  const [productArea, setProductArea] = React.useState('');
  const [role, setRole] = React.useState('');
  const [savePreference, setSavePreference] = React.useState(true);
  const [matchPreference, setMatchPreference] = React.useState('none');

  /** Gather user inputs from form and send POST request to backend
    * @param {Event} event
   */
  function handleFormSubmission(event) {
    const currentTimeInMilliseconds = new Date().getTime();
    if (validateFormInputs(role, productArea, duration,
        timeAvailableUntil, currentTimeInMilliseconds)) {
      // Override browser's default behvaior to execute POST request
      event.preventDefault();

      // Gather all form inputs into one object
      const formDetails = {
        timeAvailableUntil: timeAvailableUntil.getTime(),
        duration: duration,
        role: role,
        productArea: productArea,
        matchPreference: matchPreference,
        savePreference: savePreference,
      };

      // Send form details to AddParticipantServlet and alert user on success
      axios.post('/api/v1/add-participant', {formDetails})
          .then((response) => {
            if (response.data != null) {
              // TODO(#33): change alert to a redirection to loading page view
              alert('Successful');
            }
          });
    }
  }

  return (
    <div>
      <div className={classes.heading}>
        <h3>Choose your time preferences</h3>
      </div>
      <div className={classes.section}>
        <div className={classes.flexStartDiv}>
          <p>I am free until...</p>
          <div style={{width: 180}}>
            <MuiPickersUtilsProvider utils={DateFnsUtils}>
              <KeyboardTimePicker
                id="time-field"
                value={timeAvailableUntil}
                onChange={(value) => setTimeAvailableUntil(value)}
                KeyboardButtonProps={{'aria-label': 'time-field'}}
              />
            </MuiPickersUtilsProvider>
          </div>
        </div>
        <div className={classes.flexStartDiv}>
          <p>I want to talk for...</p>
          <DurationDropdown onChange={(value) => setDuration(value)} />
        </div>
      </div>
      <div className={classes.heading}>
        <h3>Choose your match preferences</h3>
      </div>
      <div className={classes.section}>
        <div className={classes.flexStartDiv}>
          <RoleDropdown onChange={(value) => setRole(value)} />
          <ProductAreaDropdown onChange={(value) => setProductArea(value)} />
        </div>
        <div className={classes.padding}>
          <MatchPreference onChange={(value) => setMatchPreference(value)} />
        </div>
      </div>
      <div className={classes.flexEndDiv}>
        <Checkbox onChange={(value) => setSavePreference(value)} />
        <div className={classes.flexEndDiv}>
          <Button variant="contained" color="primary"
            onClick={handleFormSubmission}>
            Submit
          </Button>
        </div>
      </div>
    </div>
  );
}
