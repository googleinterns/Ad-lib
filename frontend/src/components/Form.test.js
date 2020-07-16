import React from 'react';
import Form from './Form';
import renderer from 'react-test-renderer';
import axios from 'axios';
import mockAxios from 'axios';
import {validateFormInputs} from './Form';

beforeAll(() => {
  const DATE_TO_USE = new Date('2020');
  const mockedDate = Date;
  global.Date = jest.fn(() => DATE_TO_USE);
  global.Date.UTC = mockedDate.UTC;
  global.Date.parse = mockedDate.parse;
  global.Date.now = mockedDate.now;
});

jest.mock('axios');

describe('Form', () => {
  it('should be defined', () => {
    expect(Form).toBeDefined();
  });

  it('should render correctly in varying times and timezones', () => {
    const tree = renderer.create(<Form />).toJSON();
    expect(tree).toMatchSnapshot();
  });

  it('POST request using axios() to servlet with form details', () => {
    const mockedDate = new Date();
    const servletEndpoint = '/api/v1/add-participant';
    const mockFormDetails = {
      timeAvailableUntil: mockedDate.getTime(),
      duration: 15,
      role: 'Intern',
      productArea: 'Core',
      matchPreference: 'similar',
      savePreference: true,
    };

    axios.post(servletEndpoint, {mockFormDetails});

    expect(mockAxios.post).toHaveBeenCalledTimes(1);
    expect(mockAxios.post).toHaveBeenCalledWith(
        servletEndpoint, {mockFormDetails},
    );
  });
});

describe('Form Validation', () => {
  it('should return true', () => {
    window.alert = jest.fn();
    const role = 'Intern';
    const productArea = 'Platforms and Ecosystems';
    const timeAvailableUntilInMilliseconds = 1594846605591;
    const duration = 15;
    const currentTimeInMilliseconds = 1594801800000;
    expect(validateFormInputs(role, productArea, duration,
        timeAvailableUntilInMilliseconds, currentTimeInMilliseconds)).toBe(true);
  });

  it('should return false since Role is not specified', () => {
    window.alert = jest.fn();
    const role = '';
    const productArea = 'Platforms and Ecosystems';
    const timeAvailableUntilInMilliseconds = 1594846605591;
    const duration = 15;
    const currentTimeInMilliseconds = 1594801800000;
    expect(validateFormInputs(role, productArea, duration,
        timeAvailableUntilInMilliseconds, currentTimeInMilliseconds)).toBe(false);
  });

  it('should return false since PA is not specified', () => {
    window.alert = jest.fn();
    const role = 'Intern';
    const productArea = '';
    const timeAvailableUntilInMilliseconds = 1594846605591;
    const duration = 15;
    const currentTimeInMilliseconds = 1594801800000;
    expect(validateFormInputs(role, productArea, duration,
        timeAvailableUntilInMilliseconds, currentTimeInMilliseconds)).toBe(false);
  });

  it('should return false since time is incompatible', () => {
    window.alert = jest.fn();
    const role = 'Intern';
    const productArea = 'Core';
    const timeAvailableUntilInMilliseconds = 1594846605591;
    const duration = 15;
    const currentTimeInMilliseconds = 1594846605000;
    expect(validateFormInputs(role, productArea, duration,
        timeAvailableUntilInMilliseconds, currentTimeInMilliseconds)).toBe(false);
  });
});
