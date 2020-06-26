import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import FormControl from "@material-ui/core/FormControl";
import NativeSelect from "@material-ui/core/NativeSelect";
import TextField from "@material-ui/core/TextField";
import Button from '@material-ui/core/Button';

const useStyles = makeStyles(theme => ({
	root: {
		margin: theme.spacing(1)
	},
	formControl: {
		margin: theme.spacing(2),
		minWidth: 120
	},
	textField: {
		margin: theme.spacing(2)
	},
	divStyle: {
		display: 'flex',
		alignItems: 'flex-end',
		margin: theme.spacing(2)
	}
}));

export default function Form() {
	const classes = useStyles();
	
	return (
		<div>
			<div className={classes.divStyle}>
				<p>I'm free until...</p>
				<TextField className={classes.textField}
					id="time"
					label="Select a time"
					type="time"
					defaultValue="09:30"/>
			</div>
			<div className={classes.divStyle}>
				<p>I'm want to talk for...</p>
				<FormControl className={classes.formControl}>
					<NativeSelect
						id="duration-input"
						name="duration"
						inputProps={{ "aria-label": "duration" }}
					>
						<option value={15}>15 minutes</option>
						<option value={30}>30 minutes</option>
						<option value={45}>45 minutes</option>
						<option value={60}>60 minutes</option>
					</NativeSelect>
				</FormControl>
			</div>
			<div className={classes.root}>
				<Button variant="contained" color="primary">
					Submit
				</Button>
			</div>
		</div>
  );
}