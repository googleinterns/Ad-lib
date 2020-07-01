import React from 'react';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

export default function PreferencesCheckbox() {
  const [state, setState] = React.useState({
    checked: true
  });

  const handleChange = (event) => {
    setState({ state, [event.target.name]: event.target.checked });
  };

  return (
    <FormControlLabel
      label="Save my preferences for later"
      labelPlacement="start"
      control={
        <Checkbox
          checked={state.checked}
          onChange={handleChange}
          name="checked"
          color="primary"
        />
      }
    />
  );
}