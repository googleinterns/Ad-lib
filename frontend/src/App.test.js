import React from 'react';
import App, {fetchMatch} from './App';
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

  it('should initiate GET request using axios() to servlet', async () => {
    const mockMatchStatus = {matchStatus: 'true'};
    const mockData = {data: mockMatchStatus};
    axios.get.mockResolvedValue(mockData);

    const response = await fetchMatch();
    return expect(response).toEqual(mockMatchStatus);
  });
});
