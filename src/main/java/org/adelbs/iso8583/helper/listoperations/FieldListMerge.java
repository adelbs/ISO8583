package org.adelbs.iso8583.helper.listoperations;

import java.util.List;

import org.adelbs.iso8583.vo.FieldVO;

public interface FieldListMerge {
	public List<FieldVO> merge(final List<FieldVO> A, final List<FieldVO> B);
}
