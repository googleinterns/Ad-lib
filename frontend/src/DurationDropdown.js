import React from 'react';
import FormControl from '@material-ui/core/FormControl';
import NativeSelect from '@material-ui/core/NativeSelect';

const durations = [
  { label: '15 minutes', value: 15 },
  { label: '30 minutes', value: 30 },
  { label: '45 minutes', value: 45 },
  { label: '60 minutes', value: 60 }
];

export default function DurationDropdown(props) {
  return (
    <div>
      <FormControl style={{width: 180}}>
        <NativeSelect
          id="duration-input"
          name="duration"
          onChange={event => props.onChange(event.target.value)}
          inputProps={{ "aria-label": "duration" }}
        >
          {durations.map((currentDuration) => (
            <option value={currentDuration.value}>
              {currentDuration.label}
            </option>
          ))}  
        </NativeSelect>
      </FormControl>
    </div>
  );
}
