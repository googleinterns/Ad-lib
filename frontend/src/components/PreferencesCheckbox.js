import React from 'react';
import PropTypes from 'prop-types';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

// Add onChange to props validation
PreferencesCheckbox.propTypes = {
  onChange: PropTypes.func,
};

/**
 * Create checkbox component for input on saving preferences for future use
 * @return {PreferencesCheckbox} PreferencesCheckbox component
 * @param {Object} props
 */
export default function PreferencesCheckbox(props) {
  return (
    <FormControlLabel
      label="Save my preferences for later"
      labelPlacement="start"
      control={
        <Checkbox
          onChange={(event) => props.onChange(event.target.checked)}
          name="checkbox"
          color="primary"
          defaultChecked="true"
        />
      }
    />
  );
}
