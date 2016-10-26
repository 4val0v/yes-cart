/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
import {Component, OnInit, OnDestroy, Input, Output, EventEmitter} from '@angular/core';
import {NgIf} from '@angular/common';
import {PaymentVO} from './../../shared/model/index';
import {PaginationComponent} from './../../shared/pagination/index';
import {Config} from './../../shared/config/env.config';

@Component({
  selector: 'yc-payments',
  moduleId: module.id,
  templateUrl: 'payments.component.html',
  directives: [NgIf, PaginationComponent],
})

export class PaymentsComponent implements OnInit, OnDestroy {

  _payments:Array<PaymentVO> = [];

  filteredPayments:Array<PaymentVO>;

  @Input() selectedPayment:PaymentVO;

  @Output() dataSelected: EventEmitter<PaymentVO> = new EventEmitter<PaymentVO>();

  //paging
  maxSize:number = Config.UI_TABLE_PAGE_NUMS;
  itemsPerPage:number = Config.UI_TABLE_PAGE_SIZE;
  totalItems:number = 0;
  currentPage:number = 1;
  // Must use separate variables (not currentPage) for table since that causes
  // cyclic even update and then exception https://github.com/angular/angular/issues/6005
  pageStart:number = 0;
  pageEnd:number = this.itemsPerPage;

  constructor() {
    console.debug('PaymentsComponent constructed');
  }

  ngOnInit() {
    console.debug('PaymentsComponent ngOnInit');
  }

  @Input()
  set payments(payments:Array<PaymentVO>) {
    this._payments = payments;
    this.filterPayments();
  }

  private filterPayments() {

    this.filteredPayments = this._payments;
    console.debug('PaymentsComponent filterPayments', this.filteredPayments);

    if (this.filteredPayments === null) {
      this.filteredPayments = [];
    }

    let _total = this.filteredPayments.length;
    this.totalItems = _total;
    if (_total > 0) {
      this.resetLastPageEnd();
    }
  }

  ngOnDestroy() {
    console.debug('PaymentsComponent ngOnDestroy');
    this.selectedPayment = null;
    this.dataSelected.emit(null);
  }

  resetLastPageEnd() {
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  onPageChanged(event:any) {
    this.pageStart = (event.page - 1) * this.itemsPerPage;
    let _pageEnd = this.pageStart + this.itemsPerPage;
    if (_pageEnd > this.totalItems) {
      this.pageEnd = this.totalItems;
    } else {
      this.pageEnd = _pageEnd;
    }
  }

  protected onSelectRow(row:PaymentVO) {
    console.debug('PaymentsComponent onSelectRow handler', row);
    if (row == this.selectedPayment) {
      this.selectedPayment = null;
    } else {
      this.selectedPayment = row;
    }
    this.dataSelected.emit(this.selectedPayment);
  }

}
