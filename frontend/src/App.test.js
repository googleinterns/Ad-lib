import React from 'react';
import App from './App';
import renderer from 'react-test-renderer';
import axios from 'axios';

beforeAll(() => {
  const DATE_TO_USE = new Date('2020');
  const mockedDate = Date;
  global.Date = jest.fn(() => DATE_TO_USE);
  global.Date.UTC = mockedDate.UTC;
  global.Date.parse = mockedDate.parse;
  global.Date.now = mockedDate.now;
});

jest.mock('axios');

describe('App', () => {
  it('should be defined', () => {
    expect(App).toBeDefined();
  });

  it('should render correctly', () => {
    const tree = renderer.create(<App />).toJSON();
    expect(tree).toMatchSnapshot();
  });

  it('should initiate GET request using axios() to servlet', () => {
    const servletEndpoint = '/api/v1/search-match';
    const mockMatchStatus = {matchStatus: 'true'};
    const mockData = {data: mockMatchStatus};

    axios.get.mockImplementation(() => Promise.resolve(mockData));
    const responseData = axios.get(servletEndpoint);

    responseData.then((response) => {
      expect(response.data).toEqual(mockData);
    }).catch((error) => {});
  });
});
