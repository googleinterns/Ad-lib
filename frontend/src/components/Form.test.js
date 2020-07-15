import React from 'react';
import Form from './Form';
import renderer from 'react-test-renderer';
import axios from 'axios';
import mockAxios from 'axios';

beforeAll(() => {
  const DATE_TO_USE = new Date('2020');
  const mockedDate = Date;
  global.Date = jest.fn(() => DATE_TO_USE);
  global.Date.UTC = mockedDate.UTC;
  global.Date.parse = mockedDate.parse;
  global.Date.now = mockedDate.now;
});

describe('Form', () => {
  it('should be defined', () => {
    expect(Form).toBeDefined();
  });

  it('should render correctly in varying times and timezones', () => {
    const tree = renderer.create(<Form />).toJSON();
    expect(tree).toMatchSnapshot();
  });
});

jest.mock('axios');

test('POST request using axios() to servlet with form details', () => {
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
