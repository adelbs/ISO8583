package org.adelbs.iso8583.helper.listoperations;

import java.util.ArrayList;
import java.util.List;

import org.adelbs.iso8583.vo.FieldVO;

public class ValuePriorityMerge implements FieldListMerge {


	/**
	 * Create a list with fields that exists at both StructureList and valueList, with values from the valueList
	 * @param structureList Base structure list
	 * @param valueList Will provide values to be used to update the fields from structure.
	 */
	@Override
	public List<FieldVO> merge(final List<FieldVO> structureList, final List<FieldVO> valueList) {
		final ArrayList<FieldVO> resultantList = new ArrayList<FieldVO>();
		valueList.forEach(sourceVO->{
			structureList.forEach(targetVO->{
				if(targetVO.getBitNum().intValue() == sourceVO.getBitNum().intValue()){
					resultantList.add(sourceVO);
				}
			});
		});
		return resultantList;
	}

}
