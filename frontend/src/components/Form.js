import React from 'react';
import PropTypes from 'prop-types';
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
import InterestsDropdown from './InterestsDropdown';
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

// Add onSubmit to props validation
Form.propTypes = {
  onSubmit: PropTypes.func,
};


/**
 * Validates from inputs on submission and alerts user on error
 * @param {Number} duration in minutes
 * @param {Number} endTimeAvailableMilliseconds
 * @param {Number} currentTimeMilliseconds
 * @return {Boolean} true or false based on validity of inputs
 */
export function validateFormInputs(
    duration,
    endTimeAvailableMilliseconds,
    currentTimeMilliseconds,
) {
  const durationMilliseconds = duration * 60000;

  if (isNaN(endTimeAvailableMilliseconds)) {
    alert('Please select a valid date.');
    return false;
  } else if (currentTimeMilliseconds + durationMilliseconds >=
    endTimeAvailableMilliseconds) {
    // Check if a meeting is possible with the provided inputs
    alert('Please select an larger time availability window');
    return false;
  }
  return true;
}

/**
 * Create form component with time and match preference inputs
 * @param {Object} props
 * @return {Form} Form component
 */
export default function Form(props) {
  const classes = useStyles();

  // Declare state variables for each input field and set default states
  const [endTimeAvailable, setEndTimeAvailable] =
    React.useState(new Date());
  const [duration, setDuration] = React.useState(15);
  const [productArea, setProductArea] = React.useState('');
  const [role, setRole] = React.useState('');
  const [interests, setInterests] = React.useState([]);
  const [savePreference, setSavePreference] = React.useState(true);
  const [matchPreference, setMatchPreference] = React.useState('none');

  /**
   * Method that controls the disabled attribute of MatchPreference radio group
   * based on the completion status of the personal preference fields
   * @return {Boolean} true/false where true means the MatchPreference options
   * will be disabled and false means that the options will be enabled.
   */
  function shouldDisableMatchPreferenceFields() {
    // TO-DO(#65): Add interests to this validation
    return role === '' && productArea === '';
  }

  /** Gather user inputs from form and send POST request to backend
    * @param {Event} event
   */
  function handleFormSubmission(event) {
    const currentTimeInMilliseconds = new Date().getTime();
    if (!validateFormInputs(
        duration,
        endTimeAvailable.getTime(),
        currentTimeInMilliseconds)) {
      return;
    }
    // Override browser's default behvaior to execute POST request
    event.preventDefault();

    // Gather all form inputs into one object
    const formDetails = {
      endTimeAvailable: endTimeAvailable.getTime(),
      duration: duration,
      role: role,
      productArea: productArea,
      interests: interests,
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

    props.onSubmit();
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
                value={endTimeAvailable}
                onChange={(value) => setEndTimeAvailable(value)}
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
          <InterestsDropdown onChange={(value) => setInterests(value)} />
        </div>
        <div className={classes.padding}>
          <MatchPreference
            onChange={(value) => setMatchPreference(value)}
            shouldDisableMatchPreferenceFields={
              shouldDisableMatchPreferenceFields()}
          />
        </div>
      </div>
      <div className={classes.flexEndDiv}>
        <Checkbox onChange={(value) => setSavePreference(value)} />
        <div className={classes.flexEndDiv}>
          <Button variant="contained" color="primary"
            onClick={handleFormSubmission}>
            Match me!
          </Button>
        </div>
      </div>
    </div>
  );
}
