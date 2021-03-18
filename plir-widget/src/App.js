import Selector from './components/Selector'
import Results from './components/Results'
import { HashRouter as Router, Switch, Route } from 'react-router-dom'


function App() {

  return (
    <div className="bx--grid">
      <div className="bx--row">
        <div className="bx--col-lg-4 bx--col-md-4 bx--col-sm-4">
          <Router>
            <Switch>
              <Route exact path="/">
                <Selector />
              </Route>
              <Route path="/results/:id" children={<Results/>} />
            </Switch>
          </Router>
        </div>
      </div>
    </div>
  );
}

export default App;
