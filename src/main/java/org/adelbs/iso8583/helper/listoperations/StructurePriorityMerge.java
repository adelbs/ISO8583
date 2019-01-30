package org.adelbs.iso8583.helper.listoperations;

import java.util.ArrayList;
import java.util.List;

import org.adelbs.iso8583.vo.FieldVO;

public class StructurePriorityMerge implements FieldListMerge{

	/**
	 * Create a list with the same structure from structureList, but with values from valueList.
	 * Mantain blank fields from structureList, case its not defined at valueList.
	 * @param structureList Base structure, will have its fields updated with values from valueList
	 * @param valueList Will provide values to be used to update the fields from target
	 */
	@Override
	public List<FieldVO> merge(final List<FieldVO> structureList, final List<FieldVO> valueList) {
		final ArrayList<FieldVO> resultantList = new ArrayList<FieldVO>();
		for (final FieldVO targetVO : structureList) {
			FieldVO newFieldVO = targetVO;
			for (FieldVO sourceVO : valueList) {
				if (targetVO.getBitNum().intValue() == sourceVO.getBitNum().intValue()) {
					newFieldVO = sourceVO;
				}
			}
			resultantList.add(newFieldVO);
		}
		return resultantList;
	}
}
