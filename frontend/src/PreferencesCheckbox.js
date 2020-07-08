import React from 'react';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Checkbox from '@material-ui/core/Checkbox';

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
