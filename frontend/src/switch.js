import React from 'react';
import Switch from '@material-ui/core/Switch';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';


export default function CustomizedSwitches() {
  const [state, setState] = React.useState({
    similarityRating: true
  });

  const handleChange = (event) => {
    setState({ ...state, [event.target.name]: event.target.checked });
  };

  return (
      <Typography>
        <Grid component="label" container>
          <Grid item>No</Grid>
          <Grid item>
            <Switch 
              checked={state.similarityRating}
              onChange={handleChange} 
              name="similarityRating" 
              color="primary"
            />
          </Grid>
          <Grid item>Yes</Grid>
        </Grid>
      </Typography>
  );
}