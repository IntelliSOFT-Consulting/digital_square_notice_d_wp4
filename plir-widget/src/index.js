import React from 'react';
import ReactDOM from 'react-dom';
import './carbon-components.css';
import '@carbon/grid/scss/grid.scss';
import App from './App';
import reportWebVitals from './reportWebVitals';

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);

function renderToElements(toRender, elements) {
  for (var i = 0; i < elements.length; i++) {
    ReactDOM.render(
    <React.StrictMode>
        <toRender/>
    </React.StrictMode>,
      elements[i]
  );
}
}

// renderToElements(App, document.getElementsByClassName("tx-pvls-widget"));

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
