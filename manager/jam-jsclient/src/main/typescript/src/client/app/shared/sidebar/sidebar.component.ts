import {Component,  OnInit} from '@angular/core';
import {ACCORDION_DIRECTIVES} from 'ng2-bootstrap/ng2-bootstrap';
import {HTTP_PROVIDERS}    from '@angular/http';
import {ROUTER_DIRECTIVES} from '@angular/router';
import {ManagerVO} from '../model/index';
import {ManagementService} from '../services/index';
import {ShopListComponent} from '../shop/index';

@Component({
  selector: 'sidebar',
  moduleId: module.id,
  templateUrl: 'sidebar.component.html',
  directives: [ROUTER_DIRECTIVES, ShopListComponent, ACCORDION_DIRECTIVES],
  providers: [HTTP_PROVIDERS, ManagementService]
})

export class SidebarComponent implements OnInit {

  private currentUser : ManagerVO;
  private currentUserName : string;

  constructor (private _managementService : ManagementService) {
    console.debug('SidebarComponent constructed');
  }

  ngOnInit() {
    console.debug('SidebarComponent ngOnInit');
    var _sub:any = this._managementService.getMyself().subscribe( myself => {
      console.debug('SidebarComponent getMyself', myself);
      this.currentUser = myself;
      if (this.currentUser.firstName != null && /.*\S+.*/.test(this.currentUser.firstName)) {
        this.currentUserName = this.currentUser.firstName;
      } else if (this.currentUser.lastName != null && /.*\S+.*/.test(this.currentUser.lastName)) {
        this.currentUserName = this.currentUser.lastName;
      } else {
        this.currentUserName = this.currentUser.email;
      }
      _sub.unsubscribe();
    });

  }

}
