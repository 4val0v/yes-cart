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
import {Component, OnInit, OnChanges, Input, Output, ViewChild, EventEmitter} from '@angular/core';
import {CORE_DIRECTIVES } from '@angular/common';
import {REACTIVE_FORM_DIRECTIVES} from '@angular/forms';
import {ProductTypeVO, ProductTypeAttrVO, ProductTypeViewGroupVO} from './../../shared/model/index';
import {I18nComponent} from './../../shared/i18n/index';
import {ModalComponent, ModalResult, ModalAction} from './../../shared/modal/index';
import {FormValidationEvent, Futures, Future} from './../../shared/event/index';
import {Config} from './../../shared/config/env.config';


@Component({
  selector: 'yc-product-type-group',
  moduleId: module.id,
  templateUrl: 'product-type-group.component.html',
  directives: [REACTIVE_FORM_DIRECTIVES, CORE_DIRECTIVES, ModalComponent, I18nComponent]
})

export class ProductTypeGroupComponent implements OnInit, OnChanges {

  @Input() masterObject:ProductTypeVO;

  _groupFilter:string;
  _objectAttributes:Array<ProductTypeAttrVO>;
  _objectAttributesMap:any;
  attributeFilter:string;
  filteredObjectGroups:Array<ProductTypeViewGroupVO>;
  removedObjectGroups:Array<ProductTypeViewGroupVO>;
  filteredObjectAttributes:Array<ProductTypeAttrVO>;
  delayedGroupFiltering:Future;
  delayedAttributeFiltering:Future;
  delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  changed:boolean = false;
  validForSave:boolean = false;

  @ViewChild('deleteConfirmationModalDialog')
  deleteConfirmationModalDialog:ModalComponent;
  @ViewChild('editModalDialog')
  editModalDialog:ModalComponent;

  selectedRow:ProductTypeViewGroupVO;
  groupToEdit:ProductTypeViewGroupVO;

  selectedRowAvailable:ProductTypeAttrVO[];
  selectedRowAvailableFiltered:ProductTypeAttrVO[];
  selectedRowAssigned:ProductTypeAttrVO[];
  selectedRowAssignedExtra:string[];

  @Output() dataSelected: EventEmitter<ProductTypeViewGroupVO> = new EventEmitter<ProductTypeViewGroupVO>();
  @Output() dataChanged: EventEmitter<FormValidationEvent<ProductTypeVO>> = new EventEmitter<FormValidationEvent<ProductTypeVO>>();

  /**
   * Construct attribute panel
   */
  constructor() {
    console.debug('ProductTypeGroupComponent constructed');

    this.groupToEdit = null;
    let that = this;
    this.delayedGroupFiltering = Futures.perpetual(function() {
      that.filterGroups();
    }, this.delayedFilteringMs);
    this.delayedAttributeFiltering = Futures.perpetual(function() {
      that.filterAttributes();
    }, this.delayedFilteringMs);

  }

  @Input()
  set objectAttributes(objectAttributes:Array<ProductTypeAttrVO>) {
    this._objectAttributes = objectAttributes;
    this.loadAttributeData();
  }

  get objectAttributes():Array<ProductTypeAttrVO> {
    return this._objectAttributes;
  }

  @Input()
  set groupFilter(groupFilter:string) {
    this._groupFilter = groupFilter;
    this.delayedGroupFiltering.delay();
  }

  /** {@inheritDoc} */
  public ngOnInit() {
    console.debug('ProductTypeGroupComponent ngOnInit', this.masterObject);
  }

  ngOnChanges(changes:any) {
    console.debug('ProductTypeGroupComponent ngOnChanges', changes);
    this.delayedGroupFiltering.delay();
  }

  private loadAttributeData() {
    if (this.masterObject && this._objectAttributes) {
      this._objectAttributesMap = {};
      this._objectAttributes.forEach(attr => {
        this._objectAttributesMap[attr.attribute.code] = attr;
      });
    }
    this.removedObjectGroups = [];
  }

  private filterGroups() {
    if (this.masterObject) {

      var groups:ProductTypeViewGroupVO[] = [];
      if (this._groupFilter) {
        let _filter = this._groupFilter.toLowerCase();
        groups = this.masterObject.viewGroups.filter(group =>
          group.name && group.name.toLowerCase().indexOf(_filter) != -1
        );
      } else {
        groups = this.masterObject.viewGroups;
      }

      groups.sort(function(a, b) {
        var rank:number = a.rank - b.rank;
        if (rank == 0) {
          return a.name > b.name ? 1 : -1;
        }
        return rank;
      });

      this.filteredObjectGroups = groups;
    }
  }


  protected onSelectRow(row:ProductTypeViewGroupVO) {
    console.debug('ProductTypeGroupComponent onSelectRow handler', row);
    if (row == this.selectedRow) {
      this.selectedRow = null;
    } else {
      this.selectedRow = row;
      this.mapAttributesInGroup();
    }
    this.dataSelected.emit(this.selectedRow);
  }

  private mapAttributesInGroup() {

    if (this.selectedRow.attrCodeList == null) {
      this.selectedRow.attrCodeList = [];
    }

    let assignedCodes = this.selectedRow.attrCodeList;

    this.selectedRowAssigned = this._objectAttributes.filter(attr =>
      assignedCodes.indexOf(attr.attribute.code) != -1
    );
    this.selectedRowAvailable = this._objectAttributes.filter(attr =>
      assignedCodes.indexOf(attr.attribute.code) == -1
    );
    this.selectedRowAssignedExtra = [];
    assignedCodes.forEach(code => {
      let idx = this.selectedRowAssigned.findIndex(attr =>
        attr.attribute.code === code
      );
      if (idx == -1) {
        this.selectedRowAssignedExtra.push(code);
      }
    });

    this.filterAttributes();

  }

  private filterAttributes() {

    if (this.attributeFilter) {

      let _filter = this.attributeFilter.toLowerCase();

      this.selectedRowAvailableFiltered = this.selectedRowAvailable.filter(attr =>
        attr.attribute.code.toLowerCase().indexOf(_filter) != -1 ||
        attr.attribute.name.toLowerCase().indexOf(_filter) != -1 ||
        attr.attribute.description && attr.attribute.description.toLowerCase().indexOf(_filter) != -1
      );

    } else {

      this.selectedRowAvailableFiltered = this.selectedRowAvailable;

    }

  }


  protected getAttrFlags(row:ProductTypeAttrVO) {
    let flags = '';
    if (row.visible) {
      flags += '<i class="fa fa-eye"></i>&nbsp;';
    }
    if (row.similarity) {
      flags += '<i class="fa fa-copy"></i>&nbsp;';
    }
    if (row.store) {
      flags += '<i class="fa fa-save"></i>&nbsp;';
    }
    if (row.search) {
      if (row.primary) {
        flags += '<i class="fa fa-search-plus"></i>&nbsp;';
      } else {
        flags += '<i class="fa fa-search"></i>&nbsp;';
      }
    }
    if (row.navigation) {
      if (row.navigationType === 'R') {
        flags += '<i class="fa fa-sliders"></i>&nbsp;';
      } else {
        flags += '<i class="fa fa-list-alt"></i>&nbsp;';
      }
    }
    return flags;
  }

  protected onAssignedAttrClick(row:ProductTypeAttrVO) {
    console.debug('ProductTypeGroupComponent onAssignedAttrClick handler', row);
    let idx = this.selectedRow.attrCodeList.indexOf(row.attribute.code);
    if (idx != -1) {
      this.selectedRow.attrCodeList.splice(idx, 1);
      let idx2 = this.selectedRowAssigned.indexOf(row);
      this.selectedRowAssigned.splice(idx2, 1);
      this.selectedRowAssigned = this.selectedRowAssigned.slice(0, this.selectedRowAssigned.length);
      this.selectedRowAvailable.push(row);
      this.changed = true;
      this.filterAttributes();
      this.processDataChangesEvent();
    }
  }

  protected onAssignedAttrCodeClick(row:string) {
    console.debug('ProductTypeGroupComponent onAssignedAttrCodeClick handler', row);
    let idx = this.selectedRow.attrCodeList.indexOf(row);
    if (idx != -1) {
      this.selectedRow.attrCodeList.splice(idx, 1);
      let idx2 = this.selectedRowAssignedExtra.indexOf(row);
      this.selectedRowAssignedExtra.splice(idx2, 1);
      this.selectedRowAssignedExtra = this.selectedRowAssignedExtra.slice(0, this.selectedRowAssignedExtra.length);
      this.changed = true;
      this.processDataChangesEvent();
    }
  }

  protected onAvailableAttrClick(row:ProductTypeAttrVO) {
    console.debug('ProductTypeGroupComponent onAvailableAttrClick handler', row);
    let idx = this.selectedRow.attrCodeList.indexOf(row.attribute.code);
    if (idx == -1) {
      this.selectedRow.attrCodeList.push(row.attribute.code);
      let idx2 = this.selectedRowAvailable.indexOf(row);
      this.selectedRowAvailable.splice(idx2, 1);
      this.selectedRowAssigned.push(row);
      this.changed = true;
      this.filterAttributes();
      this.processDataChangesEvent();
    }
  }

  protected onAttributeFilterChange() {
    this.delayedAttributeFiltering.delay();
  }


  public onRowAdd() {
    console.debug('ProductTypeGroupComponent onRowAdd handler');
    this.groupToEdit = {
      prodTypeAttributeViewGroupId: 0,
      producttypeId: this.masterObject.producttypeId,
      attrCodeList: [], rank: 500, name: '', displayNames: []
    };
    this.changed = false;
    this.validForSave = false;
    this.editModalDialog.show();

  }


  protected onRowDelete(row:ProductTypeViewGroupVO) {
    console.debug('ProductTypeGroupComponent onRowDelete handler', row);
    this.deleteConfirmationModalDialog.show();
  }

  public onRowDeleteSelected() {
    if (this.selectedRow != null) {
      this.onRowDelete(this.selectedRow);
    }
  }

  protected onRowEdit(row:ProductTypeViewGroupVO) {
    console.debug('ProductTypeGroupComponent onRowEdit handler', row);
    this.groupToEdit = row;
    this.changed = false;
    this.validForSave = false;
    this.editModalDialog.show();
  }

  public onRowEditSelected() {
    if (this.selectedRow != null) {
      this.onRowEdit(this.selectedRow);
    }
  }


  onDataChange(event:any) {

    this.changed = true;
    this.validForSave = !isNaN(this.groupToEdit.rank) && this.groupToEdit.name != null && /\S+.*\S+/.test(this.groupToEdit.name);

    console.debug('ProductTypeGroupComponent data changed and ' + (this.validForSave ? 'is valid' : 'is NOT valid'), event);
  }


  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    console.debug('ProductTypeGroupComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      let attrToDelete = this.selectedRow.prodTypeAttributeViewGroupId;
      if (attrToDelete === 0) {
        let idx = this.masterObject.viewGroups.findIndex(attrVo => {
          return attrVo.name === this.selectedRow.name;
        });
        this.masterObject.viewGroups.splice(idx, 1);
        console.debug('ProductTypeGroupComponent onDeleteConfirmationResult index in array of new attribute ' + idx);
      } else {
        console.debug('ProductTypeGroupComponent onDeleteConfirmationResult attribute ' + attrToDelete);
        let idx = this.masterObject.viewGroups.findIndex(attrVo => {
          return attrVo.prodTypeAttributeViewGroupId === attrToDelete;
        });
        this.masterObject.viewGroups.splice(idx, 1);
        this.removedObjectGroups.push(this.selectedRow);
      }
      this.filterGroups();
      this.onSelectRow(this.selectedRow); // deselect
      this.changed = true;
      this.processDataChangesEvent();
    }
  }

  protected onEditModalResult(modalresult: ModalResult) {
    console.debug('ProductTypeGroupComponent onEditModalResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {
      if (this.groupToEdit.prodTypeAttributeViewGroupId === 0) { // add new
        console.debug('ProductTypeGroupComponent onEditModalResult add new attribute', this.masterObject.viewGroups);
        this.masterObject.viewGroups.push(this.groupToEdit);
      } else { // edit existing
        console.debug('ProductTypeGroupComponent onEditModalResult update existing', this.masterObject.viewGroups);
      }
      this.selectedRow = this.groupToEdit;
      this.changed = true;
      this.filterGroups();
      this.processDataChangesEvent();
    } else {
      this.groupToEdit = null;
    }
  }

  private processDataChangesEvent() {

    console.debug('ProductTypeAttributeComponent data changes', this.masterObject);
    if (this.masterObject) {

      this.dataChanged.emit({ source: this.masterObject, valid: this.validForSave });

    }

  }

}
