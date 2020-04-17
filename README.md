private void initTable2() {
        final List<Student> data = Student.getList();

        List<String[]> headList = new ArrayList<>();                                  //  表头配置（String[]{字段名， 宽度， 是否需要下拉框配置（0，1）}
        headList.add(new String[]{"Id", "80", CustomTableView.ItemEditType.textInput});
        headList.add(new String[]{"名字","120", CustomTableView.ItemEditType.textInput});
        headList.add(new String[]{"年龄","80", CustomTableView.ItemEditType.textInput});
        CustomTableView.dataSetting<Student> dataSetting = new CustomTableView.dataSetting<Student>() {
            @Override
            public String[] parse(Student task) {
                return new String[]{                                            //  配置列表显示数据②
                        String.valueOf(task.getId())
                        , String.valueOf(task.getName())
                        , String.valueOf(task.getAge())
                };
            }

            @Override
            public List<Student> getDataList() {
                return data;
            }
        };

        CustomTableView.onToolBarClick onToolBarClick = new CustomTableView.onToolBarClick() {
            @Override
            public boolean clickDelete(String serverId) {
                Student formList = Student.findFormList(data, serverId);
                if (formList != null) {
                    return data.remove(formList);
                }else {
                    return false;
                }
            }

            @Override
            public void clickSave(List<ChangeBean> save) {
                for (int i = 0; i < save.size(); i++) {
                    ChangeBean bean = save.get(i);
                    List<String> line = bean.getLine();

                    Student task = Student.findFormList(data, line.get(0));
                    boolean is_newTask = false;
                    try {
                        if (task == null) {
                            task = new Student();
                            task.setId(Integer.valueOf(line.get(0)));
                            is_newTask = true;
                        }
                        task.setName(line.get(1));
                        task.setAge(Integer.valueOf(line.get(2)));

                        if (is_newTask) {
                            Log.d("table", "插入的task属性为：");
                            data.add(task);
                        } else {
                            Log.d("table", "更新的task属性为：");
                            data.set(bean.getPosition()-1, task);
                        }

                        Log.d("table", StringUtil.printClassValue(task));
                    }catch (IndexOutOfBoundsException | NumberFormatException e){
                        e.printStackTrace();
                        Toast.makeText(context, "输入的数据类型不正确！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        CustomTableView.OnBtnClickListener onBtnClickListener = new CustomTableView.OnBtnClickListener() {
            @Override
            public CustomTableRecyclerAdapter.OnImageViewClickListener onImageViewClickListener() {
                return null;
            }

            @Override
            public CustomTableRecyclerAdapter.OnSpinnerClickListener onSpinnerClickListener() {
                return null;
            }
        };

        CustomTableView customTableView = new CustomTableView<>(context, "测试用表", headList, onToolBarClick, dataSetting, onBtnClickListener);
        customTableView.setScrollListView(true);
        customTableView.setHideId(false);
        customTableView.showTable((FrameLayout) findViewById(R.id.container));
    }
